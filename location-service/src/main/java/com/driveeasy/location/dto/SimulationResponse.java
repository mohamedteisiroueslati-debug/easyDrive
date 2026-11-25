package com.driveeasy.location.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SimulationResponse {
    private Long clientId;
    private Long vehiculeId;
    private long nombreJours;
    private BigDecimal prixJournalier;
    private String categorieClient;
    private BigDecimal remise;
    private BigDecimal montantEstime;
}
