package com.driveeasy.location.dto;

import lombok.Data;

@Data
public class ClientSnapshot {
    private Long id;
    private String cin;
    private String nom;
    private String prenom;
    private String email;
    private String categorieClient;
}
