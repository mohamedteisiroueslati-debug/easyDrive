package com.driveeasy.vehicule.dto;

import com.driveeasy.vehicule.domain.Boite;
import com.driveeasy.vehicule.domain.Carburant;
import com.driveeasy.vehicule.domain.CategorieVehicule;
import com.driveeasy.vehicule.domain.StatutVehicule;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeRequest {
    @NotBlank
    private String immatriculation;
    @NotBlank
    private String marque;
    @NotBlank
    private String modele;
    @NotNull
    private Integer annee;
    @NotBlank
    private String couleur;
    @NotNull
    private Carburant carburant;
    @NotNull
    private Boite boite;
    @NotNull
    @Min(2)
    private Integer nbPlaces;
    @NotNull
    @Min(0)
    private Integer kilometrage;
    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal prixJournalier;
    private StatutVehicule statut;
    private String imageUrl;
    @NotNull
    private CategorieVehicule categorie;
}
