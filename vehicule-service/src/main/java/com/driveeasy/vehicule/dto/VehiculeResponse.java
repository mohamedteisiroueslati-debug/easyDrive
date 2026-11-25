package com.driveeasy.vehicule.dto;

import com.driveeasy.vehicule.domain.Boite;
import com.driveeasy.vehicule.domain.Carburant;
import com.driveeasy.vehicule.domain.CategorieVehicule;
import com.driveeasy.vehicule.domain.StatutVehicule;
import com.driveeasy.vehicule.domain.Vehicule;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class VehiculeResponse {
    private Long id;
    private String immatriculation;
    private String marque;
    private String modele;
    private Integer annee;
    private String couleur;
    private Carburant carburant;
    private Boite boite;
    private Integer nbPlaces;
    private Integer kilometrage;
    private BigDecimal prixJournalier;
    private StatutVehicule statut;
    private String imageUrl;
    private CategorieVehicule categorie;

    public static VehiculeResponse from(Vehicule vehicule) {
        return VehiculeResponse.builder()
                .id(vehicule.getId())
                .immatriculation(vehicule.getImmatriculation())
                .marque(vehicule.getMarque())
                .modele(vehicule.getModele())
                .annee(vehicule.getAnnee())
                .couleur(vehicule.getCouleur())
                .carburant(vehicule.getCarburant())
                .boite(vehicule.getBoite())
                .nbPlaces(vehicule.getNbPlaces())
                .kilometrage(vehicule.getKilometrage())
                .prixJournalier(vehicule.getPrixJournalier())
                .statut(vehicule.getStatut())
                .imageUrl(vehicule.getImageUrl())
                .categorie(vehicule.getCategorie())
                .build();
    }
}
