package com.driveeasy.location.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RetourRequest {
    @NotNull
    @Min(0)
    private Integer kmRetour;
    @NotNull
    private LocalDate dateRetourEffectif;
}
