package com.driveeasy.client.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private Long id;
    private String cin;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String email;
    private String telephone;
    private String adresse;
    private String numeroPermis;
    private LocalDate dateExpirationPermis;
    private CategorieClient categorieClient;
    private LocalDate dateInscription;
    private int nombreLocationsTerminees;
}
