package com.driveeasy.location.service;

import com.driveeasy.location.client.ClientServiceClient;
import com.driveeasy.location.client.VehiculeServiceClient;
import com.driveeasy.location.domain.StatutLocation;
import com.driveeasy.location.dto.ClientSnapshot;
import com.driveeasy.location.dto.DepartRequest;
import com.driveeasy.location.dto.EligibiliteSnapshot;
import com.driveeasy.location.dto.LocationRequest;
import com.driveeasy.location.dto.LocationResponse;
import com.driveeasy.location.dto.RetourRequest;
import com.driveeasy.location.dto.VehiculeSnapshot;
import com.driveeasy.location.exception.BusinessException;
import com.driveeasy.location.exception.ConflictException;
import com.driveeasy.location.repository.InMemoryLocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LocationServiceTest {

    private LocationService service;
    private InMemoryLocationRepository repository;
    private ClientServiceClient clientClient;
    private VehiculeServiceClient vehiculeClient;
    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLocationRepository();
        clientClient = Mockito.mock(ClientServiceClient.class);
        vehiculeClient = Mockito.mock(VehiculeServiceClient.class);
        pricingService = new PricingService();
        service = new LocationService(repository, clientClient, vehiculeClient, pricingService);
    }

    private LocationRequest sampleRequest() {
        LocationRequest req = new LocationRequest();
        req.setClientId(1L);
        req.setVehiculeId(2L);
        req.setDateDebut(LocalDate.now().plusDays(1));
        req.setDateFinPrevue(LocalDate.now().plusDays(4));
        return req;
    }

    private EligibiliteSnapshot sampleEligibilite(boolean ok, String motif) {
        EligibiliteSnapshot s = new EligibiliteSnapshot();
        s.setClientId(1L);
        s.setEligible(ok);
        s.setMotif(motif);
        s.setAge(25);
        s.setPermisValide(ok);
        return s;
    }

    private ClientSnapshot sampleClient(String categorie) {
        ClientSnapshot c = new ClientSnapshot();
        c.setId(1L);
        c.setCategorieClient(categorie);
        return c;
    }

    private VehiculeSnapshot sampleVehicule(String statut, BigDecimal prix) {
        VehiculeSnapshot v = new VehiculeSnapshot();
        v.setId(2L);
        v.setStatut(statut);
        v.setPrixJournalier(prix);
        return v;
    }

    @Test
    void testCreate_ClientIneligibleThrowsBusinessException() {
        when(clientClient.getEligibilite(1L))
                .thenReturn(sampleEligibilite(false, "Permis expire"));

        assertThrows(BusinessException.class, () -> service.create(sampleRequest()));
    }

    @Test
    void testCreate_VehiculeNonDisponibleThrowsConflictException() {
        when(clientClient.getEligibilite(1L))
                .thenReturn(sampleEligibilite(true, "OK"));
        when(clientClient.getClient(1L))
                .thenReturn(sampleClient("STANDARD"));
        when(vehiculeClient.getVehicule(2L))
                .thenReturn(sampleVehicule("LOUE", new BigDecimal("100.00")));

        assertThrows(ConflictException.class, () -> service.create(sampleRequest()));
    }

    @Test
    void testCreate_Success() {
        when(clientClient.getEligibilite(1L))
                .thenReturn(sampleEligibilite(true, "OK"));
        when(clientClient.getClient(1L))
                .thenReturn(sampleClient("STANDARD"));
        when(vehiculeClient.getVehicule(2L))
                .thenReturn(sampleVehicule("DISPONIBLE", new BigDecimal("100.00")));

        LocationResponse resp = service.create(sampleRequest());
        assertNotNull(resp.getId());
        assertEquals(StatutLocation.RESERVEE, resp.getStatut());
        assertEquals(new BigDecimal("300.00"), resp.getMontantTotal());
    }

    @Test
    void testCycleComplet_DepartEtRetour() {
        when(clientClient.getEligibilite(1L))
                .thenReturn(sampleEligibilite(true, "OK"));
        when(clientClient.getClient(1L))
                .thenReturn(sampleClient("STANDARD"));
        when(vehiculeClient.getVehicule(2L))
                .thenReturn(sampleVehicule("DISPONIBLE", new BigDecimal("100.00")));

        LocationResponse reservation = service.create(sampleRequest());

        // Depart
        DepartRequest departReq = new DepartRequest();
        departReq.setKmDepart(10000);
        LocationResponse enCours = service.depart(reservation.getId(), departReq);
        assertEquals(StatutLocation.EN_COURS, enCours.getStatut());
        assertEquals(10000, enCours.getKmDepart());

        // Retour a temps
        RetourRequest retourReq = new RetourRequest();
        retourReq.setKmRetour(10500);
        retourReq.setDateRetourEffectif(sampleRequest().getDateFinPrevue());
        LocationResponse terminee = service.retour(reservation.getId(), retourReq);
        assertEquals(StatutLocation.TERMINEE, terminee.getStatut());
        assertEquals(10500, terminee.getKmRetour());
        assertEquals(new BigDecimal("0.00"), terminee.getPenalite());
    }
}
