package com.driveeasy.location.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class StatistiquesResponse {
    private BigDecimal chiffreAffaires;
    private long nombreLocationsTerminees;
    private long nombreLocationsEnCours;
    private long nombreVehicules;
    private long nombreVehiculesLoues;
    private double tauxOccupation;
    private List<TopVehicule> topVehicules;

    @Data
    @Builder
    public static class TopVehicule {
        private Long vehiculeId;
        private String immatriculation;
        private long nombreLocations;
        private BigDecimal chiffreAffaires;
    }
}
