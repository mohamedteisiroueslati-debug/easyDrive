package com.driveeasy.location.service;

import com.driveeasy.location.client.ClientServiceClient;
import com.driveeasy.location.client.VehiculeServiceClient;
import com.driveeasy.location.domain.Location;
import com.driveeasy.location.domain.StatutLocation;
import com.driveeasy.location.dto.CategorieUpdateRequest;
import com.driveeasy.location.dto.ClientSnapshot;
import com.driveeasy.location.dto.DepartRequest;
import com.driveeasy.location.dto.EligibiliteSnapshot;
import com.driveeasy.location.dto.LocationRequest;
import com.driveeasy.location.dto.LocationResponse;
import com.driveeasy.location.dto.PageResponse;
import com.driveeasy.location.dto.RetourRequest;
import com.driveeasy.location.dto.SimulationResponse;
import com.driveeasy.location.dto.StatistiquesResponse;
import com.driveeasy.location.dto.StatutUpdateRequest;
import com.driveeasy.location.dto.VehiculeSnapshot;
import com.driveeasy.location.exception.BusinessException;
import com.driveeasy.location.exception.ConflictException;
import com.driveeasy.location.exception.NotFoundException;
import com.driveeasy.location.exception.RemoteServiceException;
import com.driveeasy.location.repository.LocationRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final LocationRepository repository;
    private final ClientServiceClient clientServiceClient;
    private final VehiculeServiceClient vehiculeServiceClient;
    private final PricingService pricingService;

    @Value("${driveeasy.location.locations-pour-fidele:5}")
    private int locationsPourFidele;

    @Value("${driveeasy.location.locations-pour-vip:15}")
    private int locationsPourVip;

    public LocationService(LocationRepository repository,
                           ClientServiceClient clientServiceClient,
                           VehiculeServiceClient vehiculeServiceClient,
                           PricingService pricingService) {
        this.repository = repository;
        this.clientServiceClient = clientServiceClient;
        this.vehiculeServiceClient = vehiculeServiceClient;
        this.pricingService = pricingService;
    }

    public PageResponse<LocationResponse> search(StatutLocation statut, Long clientId, Long vehiculeId,
                                                 LocalDate dateDebut, LocalDate dateFin, int page, int size) {
        List<LocationResponse> filtered = repository.findAll().stream()
                .filter(location -> statut == null || location.getStatut() == statut)
                .filter(location -> clientId == null || location.getClientId().equals(clientId))
                .filter(location -> vehiculeId == null || location.getVehiculeId().equals(vehiculeId))
                .filter(location -> dateDebut == null || !location.getDateDebut().isBefore(dateDebut))
                .filter(location -> dateFin == null || !location.getDateFinPrevue().isAfter(dateFin))
                .sorted(Comparator.comparing(Location::getDateCreation).reversed())
                .map(LocationResponse::from)
                .toList();
        int from = Math.min(page * size, filtered.size());
        int to = Math.min(from + size, filtered.size());
        int totalPages = size == 0 ? 0 : (int) Math.ceil(filtered.size() / (double) size);
        return new PageResponse<>(filtered.subList(from, to), page, size, filtered.size(), totalPages);
    }

    public LocationResponse findById(Long id) {
        Location location = getOrThrow(id);
        return LocationResponse.from(location, safeClient(location.getClientId()), safeVehicule(location.getVehiculeId()));
    }

    public LocationResponse create(LocationRequest request) {
        validateDates(request.getDateDebut(), request.getDateFinPrevue());
        EligibiliteSnapshot eligibilite = getEligibilite(request.getClientId());
        if (!eligibilite.isEligible()) {
            throw new BusinessException("Client non eligible : " + eligibilite.getMotif());
        }
        ClientSnapshot client = getClient(request.getClientId());
        VehiculeSnapshot vehicule = getVehicule(request.getVehiculeId());
        if (!"DISPONIBLE".equalsIgnoreCase(vehicule.getStatut())) {
            throw new ConflictException("Le vehicule n'est pas disponible (statut " + vehicule.getStatut() + ")");
        }
        if (hasOverlap(request.getVehiculeId(), request.getDateDebut(), request.getDateFinPrevue(), null)) {
            throw new ConflictException("Le vehicule est deja reserve sur cette periode");
        }
        long jours = pricingService.nombreJours(request.getDateDebut(), request.getDateFinPrevue());
        BigDecimal montant = pricingService.calculerMontant(vehicule.getPrixJournalier(), jours, client.getCategorieClient());
        Location location = Location.builder()
                .clientId(request.getClientId())
                .vehiculeId(request.getVehiculeId())
                .dateDebut(request.getDateDebut())
                .dateFinPrevue(request.getDateFinPrevue())
                .prixJournalierApplique(vehicule.getPrixJournalier())
                .montantTotal(montant)
                .penalite(BigDecimal.ZERO)
                .statut(StatutLocation.RESERVEE)
                .dateCreation(LocalDateTime.now())
                .build();
        return LocationResponse.from(repository.save(location), client, vehicule);
    }

    public SimulationResponse simuler(LocationRequest request) {
        validateDates(request.getDateDebut(), request.getDateFinPrevue());
        EligibiliteSnapshot eligibilite = getEligibilite(request.getClientId());
        if (!eligibilite.isEligible()) {
            throw new BusinessException("Client non eligible : " + eligibilite.getMotif());
        }
        ClientSnapshot client = getClient(request.getClientId());
        VehiculeSnapshot vehicule = getVehicule(request.getVehiculeId());
        long jours = pricingService.nombreJours(request.getDateDebut(), request.getDateFinPrevue());
        BigDecimal remise = pricingService.remise(client.getCategorieClient());
        BigDecimal montant = pricingService.calculerMontant(vehicule.getPrixJournalier(), jours, client.getCategorieClient());
        return SimulationResponse.builder()
                .clientId(client.getId())
                .vehiculeId(vehicule.getId())
                .nombreJours(jours)
                .prixJournalier(vehicule.getPrixJournalier())
                .categorieClient(client.getCategorieClient())
                .remise(remise)
                .montantEstime(montant)
                .build();
    }

    public LocationResponse depart(Long id, DepartRequest request) {
        Location location = getOrThrow(id);
        if (location.getStatut() != StatutLocation.RESERVEE) {
            throw new BusinessException("Le depart n'est possible que pour une reservation");
        }
        location.setKmDepart(request.getKmDepart());
        location.setStatut(StatutLocation.EN_COURS);
        repository.save(location);
        try {
            vehiculeServiceClient.updateStatut(location.getVehiculeId(), new StatutUpdateRequest("LOUE"));
        } catch (FeignException ex) {
            location.setStatut(StatutLocation.RESERVEE);
            location.setKmDepart(null);
            repository.save(location);
            throw new RemoteServiceException("Echec de la mise a jour du statut vehicule, reservation restauree");
        }
        return findById(id);
    }

    public LocationResponse retour(Long id, RetourRequest request) {
        Location location = getOrThrow(id);
        if (location.getStatut() != StatutLocation.EN_COURS) {
            throw new BusinessException("Le retour n'est possible que pour une location en cours");
        }
        if (location.getKmDepart() != null && request.getKmRetour() < location.getKmDepart()) {
            throw new BusinessException("Le kilometrage de retour ne peut pas etre inferieur au kilometrage de depart");
        }
        try {
            vehiculeServiceClient.updateStatut(location.getVehiculeId(), new StatutUpdateRequest("DISPONIBLE"));
        } catch (FeignException ex) {
            throw new RemoteServiceException("Echec de la liberation du vehicule, le retour n'a pas ete enregistre");
        }
        BigDecimal penalite = pricingService.calculerPenalite(
                location.getPrixJournalierApplique(),
                location.getDateFinPrevue(),
                request.getDateRetourEffectif()
        );
        location.setKmRetour(request.getKmRetour());
        location.setDateRetourEffectif(request.getDateRetourEffectif());
        location.setPenalite(penalite);
        location.setMontantTotal(location.getMontantTotal().add(penalite));
        location.setStatut(StatutLocation.TERMINEE);
        repository.save(location);
        updateFidelite(location.getClientId());
        return findById(id);
    }

    public LocationResponse annuler(Long id) {
        Location location = getOrThrow(id);
        if (location.getStatut() != StatutLocation.RESERVEE) {
            throw new BusinessException("Seule une reservation peut etre annulee");
        }
        location.setStatut(StatutLocation.ANNULEE);
        return LocationResponse.from(repository.save(location));
    }

    public boolean hasActiveLocation(Long clientId) {
        return repository.findAll().stream()
                .anyMatch(location -> location.getClientId().equals(clientId)
                        && (location.getStatut() == StatutLocation.RESERVEE || location.getStatut() == StatutLocation.EN_COURS));
    }

    public StatistiquesResponse statistiques() {
        List<Location> all = repository.findAll();
        List<Location> terminees = all.stream().filter(location -> location.getStatut() == StatutLocation.TERMINEE).toList();
        BigDecimal ca = terminees.stream()
                .map(Location::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long enCours = all.stream().filter(location -> location.getStatut() == StatutLocation.EN_COURS).count();
        List<VehiculeSnapshot> vehicules = List.of();
        try {
            vehicules = vehiculeServiceClient.getVehicules();
        } catch (FeignException ignored) {
            // les stats vehicules restent a 0 si le service est indisponible
        }
        long loues = vehicules.stream().filter(vehicule -> "LOUE".equalsIgnoreCase(vehicule.getStatut())).count();
        double taux = vehicules.isEmpty() ? 0 : (loues * 100.0) / vehicules.size();
        Map<Long, List<Location>> parVehicule = terminees.stream().collect(Collectors.groupingBy(Location::getVehiculeId));
        List<StatistiquesResponse.TopVehicule> top = parVehicule.entrySet().stream()
                .map(entry -> StatistiquesResponse.TopVehicule.builder()
                        .vehiculeId(entry.getKey())
                        .immatriculation(safeImmatriculation(entry.getKey()))
                        .nombreLocations(entry.getValue().size())
                        .chiffreAffaires(entry.getValue().stream().map(Location::getMontantTotal).reduce(BigDecimal.ZERO, BigDecimal::add))
                        .build())
                .sorted(Comparator.comparingLong(StatistiquesResponse.TopVehicule::getNombreLocations).reversed())
                .limit(5)
                .toList();
        return StatistiquesResponse.builder()
                .chiffreAffaires(ca)
                .nombreLocationsTerminees(terminees.size())
                .nombreLocationsEnCours(enCours)
                .nombreVehicules(vehicules.size())
                .nombreVehiculesLoues(loues)
                .tauxOccupation(Math.round(taux * 100.0) / 100.0)
                .topVehicules(top)
                .build();
    }

    private void updateFidelite(Long clientId) {
        long terminees = repository.findAll().stream()
                .filter(location -> location.getClientId().equals(clientId) && location.getStatut() == StatutLocation.TERMINEE)
                .count();
        String categorie = "STANDARD";
        if (terminees >= locationsPourVip) {
            categorie = "VIP";
        } else if (terminees >= locationsPourFidele) {
            categorie = "FIDELE";
        }
        try {
            clientServiceClient.updateCategorie(clientId, new CategorieUpdateRequest(categorie, (int) terminees));
        } catch (FeignException ex) {
            throw new RemoteServiceException("Location terminee mais echec de la mise a jour de la fidelite client");
        }
    }

    private boolean hasOverlap(Long vehiculeId, LocalDate debut, LocalDate fin, Long excludeId) {
        return repository.findAll().stream()
                .filter(location -> excludeId == null || !location.getId().equals(excludeId))
                .filter(location -> location.getVehiculeId().equals(vehiculeId))
                .filter(location -> location.getStatut() == StatutLocation.RESERVEE || location.getStatut() == StatutLocation.EN_COURS)
                .anyMatch(location -> debut.isBefore(location.getDateFinPrevue()) && fin.isAfter(location.getDateDebut()));
    }

    private void validateDates(LocalDate debut, LocalDate fin) {
        if (!fin.isAfter(debut)) {
            throw new BusinessException("La date de fin prevue doit etre posterieure a la date de debut");
        }
    }

    private Location getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location introuvable : " + id));
    }

    private EligibiliteSnapshot getEligibilite(Long clientId) {
        try {
            return clientServiceClient.getEligibilite(clientId);
        } catch (FeignException.NotFound ex) {
            throw new NotFoundException("Client introuvable : " + clientId);
        } catch (FeignException ex) {
            throw new RemoteServiceException("Impossible de verifier l'eligibilite du client");
        }
    }

    private ClientSnapshot getClient(Long clientId) {
        try {
            return clientServiceClient.getClient(clientId);
        } catch (FeignException.NotFound ex) {
            throw new NotFoundException("Client introuvable : " + clientId);
        } catch (FeignException ex) {
            throw new RemoteServiceException("Impossible de recuperer le client");
        }
    }

    private VehiculeSnapshot getVehicule(Long vehiculeId) {
        try {
            return vehiculeServiceClient.getVehicule(vehiculeId);
        } catch (FeignException.NotFound ex) {
            throw new NotFoundException("Vehicule introuvable : " + vehiculeId);
        } catch (FeignException ex) {
            throw new RemoteServiceException("Impossible de recuperer le vehicule");
        }
    }

    private ClientSnapshot safeClient(Long clientId) {
        try {
            return clientServiceClient.getClient(clientId);
        } catch (FeignException ex) {
            return null;
        }
    }

    private VehiculeSnapshot safeVehicule(Long vehiculeId) {
        try {
            return vehiculeServiceClient.getVehicule(vehiculeId);
        } catch (FeignException ex) {
            return null;
        }
    }

    private String safeImmatriculation(Long vehiculeId) {
        VehiculeSnapshot vehicule = safeVehicule(vehiculeId);
        return vehicule == null ? null : vehicule.getImmatriculation();
    }
}
