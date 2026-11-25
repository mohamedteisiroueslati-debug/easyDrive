package com.driveeasy.client.dto;

import com.driveeasy.client.domain.CategorieClient;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategorieUpdateRequest {
    @NotNull
    private CategorieClient categorie;
    private Integer nombreLocationsTerminees;
}
