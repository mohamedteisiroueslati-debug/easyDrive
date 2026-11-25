package com.driveeasy.location.dto;

import lombok.Data;

@Data
public class EligibiliteSnapshot {
    private Long clientId;
    private boolean eligible;
    private String motif;
    private int age;
    private boolean permisValide;
}
