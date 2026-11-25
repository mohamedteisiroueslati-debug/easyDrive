package com.driveeasy.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorieUpdateRequest {
    private String categorie;
    private Integer nombreLocationsTerminees;
}
