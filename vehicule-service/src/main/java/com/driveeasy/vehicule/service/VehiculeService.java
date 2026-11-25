package com.driveeasy.vehicule.service;

import com.driveeasy.vehicule.domain.Carburant;
import com.driveeasy.vehicule.domain.CategorieVehicule;
import com.driveeasy.vehicule.domain.StatutVehicule;
import com.driveeasy.vehicule.domain.Vehicule;
import com.driveeasy.vehicule.dto.StatutUpdateRequest;
import com.driveeasy.vehicule.dto.VehiculeRequest;
import com.driveeasy.vehicule.dto.VehiculeResponse;
import com.driveeasy.vehicule.exception.BusinessException;
import com.driveeasy.vehicule.exception.ConflictException;
import com.driveeasy.vehicule.exception.NotFoundException;
import com.driveeasy.vehicule.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

@Service
public class VehiculeService {

    private final VehiculeRepository repository;

    public VehiculeService(VehiculeRepository repository) {
        this.repository = repository;
    }

    public List<VehiculeResponse> findAll(CategorieVehicule categorie, Carburant carburant,
                                         StatutVehicule statut, BigDecimal prixMax) {
        return repository.findAll().stream()
                .filter(vehicule -> categorie == null || vehicule.getCategorie() == categorie)
                .filter(vehicule -> carburant == null || vehicule.getCarburant() == carburant)
                .filter(vehicule -> statut == null || vehicule.getStatut() == statut)
                .filter(vehicule -> prixMax == null || vehicule.getPrixJournalier().compareTo(prixMax) <= 0)
                .map(VehiculeResponse::from)
                .toList();
    }

    public VehiculeResponse findById(Long id) {
        return VehiculeResponse.from(getOrThrow(id));
    }

    public VehiculeResponse create(VehiculeRequest request) {
        validate(request, null);
        Vehicule saved = repository.save(toEntity(request, null));
        return VehiculeResponse.from(saved);
    }

    public VehiculeResponse update(Long id, VehiculeRequest request) {
        Vehicule existing = getOrThrow(id);
        validate(request, id);
        Vehicule updated = toEntity(request, existing.getId());
        if (updated.getStatut() == null) {
            updated.setStatut(existing.getStatut());
        }
        return VehiculeResponse.from(repository.save(updated));
    }

    public void delete(Long id) {
        Vehicule existing = getOrThrow(id);
        if (existing.getStatut() == StatutVehicule.LOUE) {
            throw new ConflictException("Suppression refusee : le vehicule est actuellement loue");
        }
        repository.deleteById(id);
    }

    public VehiculeResponse updateStatut(Long id, StatutUpdateRequest request) {
        Vehicule existing = getOrThrow(id);
        existing.setStatut(request.getStatut());
        return VehiculeResponse.from(repository.save(existing));
    }

    public List<VehiculeResponse> findDisponibles() {
        return repository.findAll().stream()
                .filter(vehicule -> vehicule.getStatut() == StatutVehicule.DISPONIBLE)
                .map(VehiculeResponse::from)
                .toList();
    }

    private Vehicule getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicule introuvable : " + id));
    }

    private void validate(VehiculeRequest request, Long currentId) {
        if (request.getPrixJournalier() == null || request.getPrixJournalier().signum() <= 0) {
            throw new BusinessException("Le prix journalier doit etre strictement positif");
        }
        int anneeCourante = Year.now().getValue();
        if (request.getAnnee() == null || request.getAnnee() > anneeCourante) {
            throw new BusinessException("L'annee du vehicule doit etre inferieure ou egale a " + anneeCourante);
        }
        repository.findByImmatriculation(request.getImmatriculation())
                .filter(vehicule -> currentId == null || !vehicule.getId().equals(currentId))
                .ifPresent(vehicule -> {
                    throw new ConflictException("Immatriculation deja utilisee : " + request.getImmatriculation());
                });
    }

    private Vehicule toEntity(VehiculeRequest request, Long id) {
        return Vehicule.builder()
                .id(id)
                .immatriculation(request.getImmatriculation())
                .marque(request.getMarque())
                .modele(request.getModele())
                .annee(request.getAnnee())
                .couleur(request.getCouleur())
                .carburant(request.getCarburant())
                .boite(request.getBoite())
                .nbPlaces(request.getNbPlaces())
                .kilometrage(request.getKilometrage())
                .prixJournalier(request.getPrixJournalier())
                .statut(request.getStatut() == null ? StatutVehicule.DISPONIBLE : request.getStatut())
                .imageUrl(request.getImageUrl())
                .categorie(request.getCategorie())
                .build();
    }
}
