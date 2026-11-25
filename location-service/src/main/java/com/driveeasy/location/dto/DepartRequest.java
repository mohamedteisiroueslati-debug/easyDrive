package com.driveeasy.location.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DepartRequest {
    @NotNull
    @Min(0)
    private Integer kmDepart;
}
