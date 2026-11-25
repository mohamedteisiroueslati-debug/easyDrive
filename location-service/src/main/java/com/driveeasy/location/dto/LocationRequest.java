package com.driveeasy.location.dto;

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
public class LocationRequest {
    @NotNull
    private Long clientId;
    @NotNull
    private Long vehiculeId;
    @NotNull
    private LocalDate dateDebut;
    @NotNull
    private LocalDate dateFinPrevue;
}
