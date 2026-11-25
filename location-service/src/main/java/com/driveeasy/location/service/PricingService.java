package com.driveeasy.location.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PricingService {

    @Value("${driveeasy.location.remise-fidele:0.05}")
    private BigDecimal remiseFidele = new BigDecimal("0.05");

    @Value("${driveeasy.location.remise-vip:0.10}")
    private BigDecimal remiseVip = new BigDecimal("0.10");

    @Value("${driveeasy.location.coefficient-penalite:1.5}")
    private BigDecimal coefficientPenalite = new BigDecimal("1.5");

    public long nombreJours(LocalDate debut, LocalDate fin) {
        long jours = ChronoUnit.DAYS.between(debut, fin);
        return Math.max(jours, 1);
    }

    public BigDecimal remise(String categorieClient) {
        if ("VIP".equalsIgnoreCase(categorieClient)) {
            return remiseVip;
        }
        if ("FIDELE".equalsIgnoreCase(categorieClient)) {
            return remiseFidele;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculerMontant(BigDecimal prixJournalier, long jours, String categorieClient) {
        BigDecimal brut = prixJournalier.multiply(BigDecimal.valueOf(jours));
        BigDecimal montantRemise = brut.multiply(remise(categorieClient));
        return brut.subtract(montantRemise).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculerPenalite(BigDecimal prixJournalier, LocalDate dateFinPrevue, LocalDate dateRetour) {
        long retard = ChronoUnit.DAYS.between(dateFinPrevue, dateRetour);
        if (retard <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return prixJournalier
                .multiply(coefficientPenalite)
                .multiply(BigDecimal.valueOf(retard))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
