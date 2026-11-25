package com.driveeasy.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EligibiliteResponse {
    private Long clientId;
    private boolean eligible;
    private String motif;
    private int age;
    private boolean permisValide;
}
