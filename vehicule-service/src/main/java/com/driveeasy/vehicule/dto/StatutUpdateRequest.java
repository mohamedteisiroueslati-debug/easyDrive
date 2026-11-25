package com.driveeasy.vehicule.dto;

import com.driveeasy.vehicule.domain.StatutVehicule;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatutUpdateRequest {
    @NotNull
    private StatutVehicule statut;
}
