package com.driveeasy.vehicule.service;

import com.driveeasy.vehicule.domain.Carburant;
import com.driveeasy.vehicule.domain.CategorieVehicule;
import com.driveeasy.vehicule.domain.StatutVehicule;
import com.driveeasy.vehicule.dto.VehiculeRequest;
import com.driveeasy.vehicule.dto.VehiculeResponse;
import com.driveeasy.vehicule.exception.BusinessException;
import com.driveeasy.vehicule.exception.ConflictException;
import com.driveeasy.vehicule.repository.InMemoryVehiculeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class VehiculeServiceTest {

    private VehiculeService service;
    private InMemoryVehiculeRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryVehiculeRepository();
        service = new VehiculeService(repository);
    }

    private VehiculeRequest sampleRequest(String immat, BigDecimal prix, Integer annee) {
        return VehiculeRequest.builder()
                .immatriculation(immat)
                .marque("Renault")
                .modele("Clio")
                .annee(annee)
                .couleur("Blanc")
                .carburant(Carburant.ESSENCE)
                .boite(com.driveeasy.vehicule.domain.Boite.MANUELLE)
                .nbPlaces(5)
                .kilometrage(15000)
                .prixJournalier(prix)
                .statut(StatutVehicule.DISPONIBLE)
                .categorie(CategorieVehicule.COMPACTE)
                .build();
    }

    @Test
    void testCreate_Success() {
        VehiculeRequest req = sampleRequest("200-TUN-123", new BigDecimal("90.00"), 2023);
        VehiculeResponse res = service.create(req);
        assertNotNull(res.getId());
        assertEquals("200-TUN-123", res.getImmatriculation());
        assertEquals(StatutVehicule.DISPONIBLE, res.getStatut());
    }

    @Test
    void testCreate_DuplicateImmatriculationThrowsConflict() {
        VehiculeRequest req = sampleRequest("200-TUN-123", new BigDecimal("90.00"), 2023);
        service.create(req);

        assertThrows(ConflictException.class, () -> service.create(req));
    }

    @Test
    void testCreate_InvalidPriceThrowsBusinessException() {
        VehiculeRequest req = sampleRequest("200-TUN-456", new BigDecimal("0.00"), 2023);
        assertThrows(BusinessException.class, () -> service.create(req));
    }

    @Test
    void testCreate_FutureYearThrowsBusinessException() {
        int futureYear = Year.now().getValue() + 5;
        VehiculeRequest req = sampleRequest("200-TUN-789", new BigDecimal("100.00"), futureYear);
        assertThrows(BusinessException.class, () -> service.create(req));
    }

    @Test
    void testDelete_RefusedWhenLoue() {
        VehiculeRequest req = sampleRequest("200-TUN-999", new BigDecimal("120.00"), 2023);
        req.setStatut(StatutVehicule.LOUE);
        VehiculeResponse created = service.create(req);

        assertThrows(ConflictException.class, () -> service.delete(created.getId()));
    }
}
