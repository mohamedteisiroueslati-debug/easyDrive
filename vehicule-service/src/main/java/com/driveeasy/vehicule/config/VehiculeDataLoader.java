package com.driveeasy.vehicule.config;

import com.driveeasy.vehicule.domain.Boite;
import com.driveeasy.vehicule.domain.Carburant;
import com.driveeasy.vehicule.domain.CategorieVehicule;
import com.driveeasy.vehicule.domain.StatutVehicule;
import com.driveeasy.vehicule.domain.Vehicule;
import com.driveeasy.vehicule.repository.VehiculeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VehiculeDataLoader {

    private final VehiculeRepository repository;

    public VehiculeDataLoader(VehiculeRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void load() {
        repository.save(Vehicule.builder()
                .immatriculation("123TUN456")
                .marque("Toyota")
                .modele("Yaris")
                .annee(2022)
                .couleur("Blanc")
                .carburant(Carburant.ESSENCE)
                .boite(Boite.MANUELLE)
                .nbPlaces(5)
                .kilometrage(32000)
                .prixJournalier(new BigDecimal("90.00"))
                .statut(StatutVehicule.DISPONIBLE)
                .categorie(CategorieVehicule.ECONOMIQUE)
                .imageUrl("https://via.placeholder.com/300x180?text=Yaris")
                .build());
        repository.save(Vehicule.builder()
                .immatriculation("789TUN012")
                .marque("BMW")
                .modele("320i")
                .annee(2023)
                .couleur("Noir")
                .carburant(Carburant.ESSENCE)
                .boite(Boite.AUTOMATIQUE)
                .nbPlaces(5)
                .kilometrage(18000)
                .prixJournalier(new BigDecimal("220.00"))
                .statut(StatutVehicule.DISPONIBLE)
                .categorie(CategorieVehicule.BERLINE)
                .imageUrl("https://via.placeholder.com/300x180?text=BMW")
                .build());
        repository.save(Vehicule.builder()
                .immatriculation("456TUN789")
                .marque("Dacia")
                .modele("Duster")
                .annee(2021)
                .couleur("Gris")
                .carburant(Carburant.DIESEL)
                .boite(Boite.MANUELLE)
                .nbPlaces(5)
                .kilometrage(54000)
                .prixJournalier(new BigDecimal("130.00"))
                .statut(StatutVehicule.MAINTENANCE)
                .categorie(CategorieVehicule.SUV)
                .imageUrl("https://via.placeholder.com/300x180?text=Duster")
                .build());
    }
}
