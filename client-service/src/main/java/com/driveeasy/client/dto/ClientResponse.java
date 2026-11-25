package com.driveeasy.client.dto;

import com.driveeasy.client.domain.CategorieClient;
import com.driveeasy.client.domain.Client;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ClientResponse {
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

    public static ClientResponse from(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .cin(client.getCin())
                .nom(client.getNom())
                .prenom(client.getPrenom())
                .dateNaissance(client.getDateNaissance())
                .email(client.getEmail())
                .telephone(client.getTelephone())
                .adresse(client.getAdresse())
                .numeroPermis(client.getNumeroPermis())
                .dateExpirationPermis(client.getDateExpirationPermis())
                .categorieClient(client.getCategorieClient())
                .dateInscription(client.getDateInscription())
                .nombreLocationsTerminees(client.getNombreLocationsTerminees())
                .build();
    }
}
