package com.driveeasy.client.service;

import com.driveeasy.client.client.LocationServiceClient;
import com.driveeasy.client.domain.CategorieClient;
import com.driveeasy.client.domain.Client;
import com.driveeasy.client.dto.CategorieUpdateRequest;
import com.driveeasy.client.dto.ClientRequest;
import com.driveeasy.client.dto.ClientResponse;
import com.driveeasy.client.dto.EligibiliteResponse;
import com.driveeasy.client.exception.ConflictException;
import com.driveeasy.client.exception.NotFoundException;
import com.driveeasy.client.repository.ClientRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;

@Service
public class ClientService {

    private final ClientRepository repository;
    private final LocationServiceClient locationServiceClient;

    public ClientService(ClientRepository repository, LocationServiceClient locationServiceClient) {
        this.repository = repository;
        this.locationServiceClient = locationServiceClient;
    }

    public List<ClientResponse> search(String nom, String cin, String email) {
        return repository.findAll().stream()
                .filter(client -> nom == null || containsIgnoreCase(client.getNom(), nom) || containsIgnoreCase(client.getPrenom(), nom))
                .filter(client -> cin == null || client.getCin().equalsIgnoreCase(cin))
                .filter(client -> email == null || client.getEmail().equalsIgnoreCase(email))
                .map(ClientResponse::from)
                .toList();
    }

    public ClientResponse findById(Long id) {
        return ClientResponse.from(getOrThrow(id));
    }

    public ClientResponse create(ClientRequest request) {
        validateUniqueness(request, null);
        Client client = toEntity(request, null);
        client.setDateInscription(LocalDate.now());
        client.setCategorieClient(request.getCategorieClient() == null ? CategorieClient.STANDARD : request.getCategorieClient());
        client.setNombreLocationsTerminees(0);
        return ClientResponse.from(repository.save(client));
    }

    public ClientResponse update(Long id, ClientRequest request) {
        Client existing = getOrThrow(id);
        validateUniqueness(request, id);
        Client updated = toEntity(request, id);
        updated.setDateInscription(existing.getDateInscription());
        updated.setCategorieClient(request.getCategorieClient() == null ? existing.getCategorieClient() : request.getCategorieClient());
        updated.setNombreLocationsTerminees(existing.getNombreLocationsTerminees());
        return ClientResponse.from(repository.save(updated));
    }

    public void delete(Long id) {
        getOrThrow(id);
        if (hasActiveLocation(id)) {
            throw new ConflictException("Suppression refusee : le client a une location active");
        }
        repository.deleteById(id);
    }

    public EligibiliteResponse eligibilite(Long id) {
        Client client = getOrThrow(id);
        int age = Period.between(client.getDateNaissance(), LocalDate.now()).getYears();
        boolean permisValide = client.getDateExpirationPermis() != null
                && !client.getDateExpirationPermis().isBefore(LocalDate.now());
        boolean eligible = age >= 21 && permisValide;
        String motif;
        if (eligible) {
            motif = "Client eligible a la location";
        } else if (age < 21) {
            motif = "Age insuffisant (minimum 21 ans)";
        } else {
            motif = "Permis de conduire expire";
        }
        return new EligibiliteResponse(client.getId(), eligible, motif, age, permisValide);
    }

    public ClientResponse updateCategorie(Long id, CategorieUpdateRequest request) {
        Client client = getOrThrow(id);
        client.setCategorieClient(request.getCategorie());
        if (request.getNombreLocationsTerminees() != null) {
            client.setNombreLocationsTerminees(request.getNombreLocationsTerminees());
        }
        return ClientResponse.from(repository.save(client));
    }

    private boolean hasActiveLocation(Long clientId) {
        try {
            return locationServiceClient.hasActiveLocation(clientId);
        } catch (FeignException ex) {
            throw new ConflictException("Impossible de verifier les locations actives du client");
        }
    }

    private Client getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable : " + id));
    }

    private void validateUniqueness(ClientRequest request, Long currentId) {
        repository.findByCin(request.getCin())
                .filter(client -> currentId == null || !client.getId().equals(currentId))
                .ifPresent(client -> {
                    throw new ConflictException("CIN deja utilise : " + request.getCin());
                });
        repository.findByEmail(request.getEmail())
                .filter(client -> currentId == null || !client.getId().equals(currentId))
                .ifPresent(client -> {
                    throw new ConflictException("Email deja utilise : " + request.getEmail());
                });
        repository.findByNumeroPermis(request.getNumeroPermis())
                .filter(client -> currentId == null || !client.getId().equals(currentId))
                .ifPresent(client -> {
                    throw new ConflictException("Numero de permis deja utilise : " + request.getNumeroPermis());
                });
    }

    private Client toEntity(ClientRequest request, Long id) {
        return Client.builder()
                .id(id)
                .cin(request.getCin())
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .dateNaissance(request.getDateNaissance())
                .email(request.getEmail())
                .telephone(request.getTelephone())
                .adresse(request.getAdresse())
                .numeroPermis(request.getNumeroPermis())
                .dateExpirationPermis(request.getDateExpirationPermis())
                .build();
    }

    private boolean containsIgnoreCase(String source, String value) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(value.toLowerCase(Locale.ROOT));
    }
}
