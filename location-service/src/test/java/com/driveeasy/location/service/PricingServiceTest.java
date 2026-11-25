package com.driveeasy.location.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceTest {

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new PricingService();
    }

    @Test
    void testNombreJours() {
        LocalDate debut = LocalDate.of(2026, 6, 1);
        LocalDate fin = LocalDate.of(2026, 6, 5);
        assertEquals(4, pricingService.nombreJours(debut, fin));
    }

    @Test
    void testCalculerMontant_Standard() {
        BigDecimal total = pricingService.calculerMontant(new BigDecimal("100.00"), 3, "STANDARD");
        assertEquals(new BigDecimal("300.00"), total);
    }

    @Test
    void testCalculerMontant_Fidele() {
        // 5% discount on 300 = 285.00
        BigDecimal total = pricingService.calculerMontant(new BigDecimal("100.00"), 3, "FIDELE");
        assertEquals(new BigDecimal("285.00"), total);
    }

    @Test
    void testCalculerMontant_Vip() {
        // 10% discount on 300 = 270.00
        BigDecimal total = pricingService.calculerMontant(new BigDecimal("100.00"), 3, "VIP");
        assertEquals(new BigDecimal("270.00"), total);
    }

    @Test
    void testCalculerPenalite_EnRetard() {
        LocalDate prevue = LocalDate.of(2026, 6, 5);
        LocalDate effectif = LocalDate.of(2026, 6, 7); // 2 days late
        // 2 days * 100 * 1.5 = 300.00
        BigDecimal penalite = pricingService.calculerPenalite(new BigDecimal("100.00"), prevue, effectif);
        assertEquals(new BigDecimal("300.00"), penalite);
    }

    @Test
    void testCalculerPenalite_ALHeure() {
        LocalDate prevue = LocalDate.of(2026, 6, 5);
        LocalDate effectif = LocalDate.of(2026, 6, 5);
        BigDecimal penalite = pricingService.calculerPenalite(new BigDecimal("100.00"), prevue, effectif);
        assertEquals(new BigDecimal("0.00"), penalite);
    }
}
