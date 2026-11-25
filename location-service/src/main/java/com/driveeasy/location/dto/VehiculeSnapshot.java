package com.driveeasy.location.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehiculeSnapshot {
    private Long id;
    private String immatriculation;
    private String marque;
    private String modele;
    private String statut;
    private BigDecimal prixJournalier;
}
