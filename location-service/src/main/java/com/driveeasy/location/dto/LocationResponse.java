package com.driveeasy.location.dto;

import com.driveeasy.location.domain.Location;
import com.driveeasy.location.domain.StatutLocation;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class LocationResponse {
    private Long id;
    private Long clientId;
    private Long vehiculeId;
    private LocalDate dateDebut;
    private LocalDate dateFinPrevue;
    private LocalDate dateRetourEffectif;
    private Integer kmDepart;
    private Integer kmRetour;
    private BigDecimal prixJournalierApplique;
    private BigDecimal montantTotal;
    private BigDecimal penalite;
    private StatutLocation statut;
    private LocalDateTime dateCreation;
    private ClientSnapshot client;
    private VehiculeSnapshot vehicule;

    public static LocationResponse from(Location location) {
        return from(location, null, null);
    }

    public static LocationResponse from(Location location, ClientSnapshot client, VehiculeSnapshot vehicule) {
        return LocationResponse.builder()
                .id(location.getId())
                .clientId(location.getClientId())
                .vehiculeId(location.getVehiculeId())
                .dateDebut(location.getDateDebut())
                .dateFinPrevue(location.getDateFinPrevue())
                .dateRetourEffectif(location.getDateRetourEffectif())
                .kmDepart(location.getKmDepart())
                .kmRetour(location.getKmRetour())
                .prixJournalierApplique(location.getPrixJournalierApplique())
                .montantTotal(location.getMontantTotal())
                .penalite(location.getPenalite())
                .statut(location.getStatut())
                .dateCreation(location.getDateCreation())
                .client(client)
                .vehicule(vehicule)
                .build();
    }
}
