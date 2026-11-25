package com.driveeasy.client.config;

import com.driveeasy.client.domain.CategorieClient;
import com.driveeasy.client.domain.Client;
import com.driveeasy.client.repository.ClientRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ClientDataLoader {

    private final ClientRepository repository;

    public ClientDataLoader(ClientRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void load() {
        repository.save(Client.builder()
                .cin("09876543")
                .nom("Ben Ali")
                .prenom("Sami")
                .dateNaissance(LocalDate.of(1995, 3, 12))
                .email("sami.benali@driveeasy.tn")
                .telephone("22123456")
                .adresse("Tunis")
                .numeroPermis("P-111222")
                .dateExpirationPermis(LocalDate.now().plusYears(3))
                .categorieClient(CategorieClient.STANDARD)
                .dateInscription(LocalDate.now().minusMonths(8))
                .nombreLocationsTerminees(2)
                .build());
        repository.save(Client.builder()
                .cin("11223344")
                .nom("Trabelsi")
                .prenom("Amira")
                .dateNaissance(LocalDate.of(1988, 7, 4))
                .email("amira.trabelsi@driveeasy.tn")
                .telephone("98765432")
                .adresse("Sfax")
                .numeroPermis("P-333444")
                .dateExpirationPermis(LocalDate.now().plusYears(2))
                .categorieClient(CategorieClient.FIDELE)
                .dateInscription(LocalDate.now().minusYears(2))
                .nombreLocationsTerminees(7)
                .build());
        repository.save(Client.builder()
                .cin("55667788")
                .nom("Gharbi")
                .prenom("Youssef")
                .dateNaissance(LocalDate.of(2008, 1, 20))
                .email("youssef.gharbi@driveeasy.tn")
                .telephone("50111222")
                .adresse("Sousse")
                .numeroPermis("P-555666")
                .dateExpirationPermis(LocalDate.now().minusMonths(1))
                .categorieClient(CategorieClient.STANDARD)
                .dateInscription(LocalDate.now().minusDays(10))
                .nombreLocationsTerminees(0)
                .build());
    }
}
