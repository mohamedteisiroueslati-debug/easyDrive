package com.driveeasy.client.dto;

import com.driveeasy.client.domain.CategorieClient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {
    @NotBlank
    private String cin;
    @NotBlank
    private String nom;
    @NotBlank
    private String prenom;
    @NotNull
    private LocalDate dateNaissance;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String telephone;
    @NotBlank
    private String adresse;
    @NotBlank
    private String numeroPermis;
    @NotNull
    private LocalDate dateExpirationPermis;
    private CategorieClient categorieClient;
}
