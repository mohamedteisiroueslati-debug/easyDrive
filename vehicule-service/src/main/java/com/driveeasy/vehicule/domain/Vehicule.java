package com.driveeasy.vehicule.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicule {
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
}
