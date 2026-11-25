package com.driveeasy.client.service;

import com.driveeasy.client.client.LocationServiceClient;
import com.driveeasy.client.domain.CategorieClient;
import com.driveeasy.client.dto.ClientRequest;
import com.driveeasy.client.dto.ClientResponse;
import com.driveeasy.client.dto.EligibiliteResponse;
import com.driveeasy.client.exception.ConflictException;
import com.driveeasy.client.repository.InMemoryClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ClientServiceTest {

    private ClientService service;
    private InMemoryClientRepository repository;
    private LocationServiceClient locationClient;

    @BeforeEach
    void setUp() {
        repository = new InMemoryClientRepository();
        locationClient = Mockito.mock(LocationServiceClient.class);
        service = new ClientService(repository, locationClient);
    }

    private ClientRequest sampleRequest(String cin, String email, String permis, LocalDate dob, LocalDate expPermis) {
        return ClientRequest.builder()
                .cin(cin)
                .nom("Ben Romdhane")
                .prenom("Karim")
                .dateNaissance(dob)
                .email(email)
                .telephone("20123456")
                .adresse("Tunis")
                .numeroPermis(permis)
                .dateExpirationPermis(expPermis)
                .categorieClient(CategorieClient.STANDARD)
                .build();
    }

    @Test
    void testCreate_Success() {
        ClientRequest req = sampleRequest("08123456", "karim@driveeasy.tn", "PERM-001",
                LocalDate.of(1995, 5, 20), LocalDate.now().plusYears(3));
        ClientResponse res = service.create(req);
        assertNotNull(res.getId());
        assertEquals("08123456", res.getCin());
        assertEquals(CategorieClient.STANDARD, res.getCategorieClient());
    }

    @Test
    void testCreate_DuplicateCinThrowsConflict() {
        ClientRequest req1 = sampleRequest("08123456", "karim1@driveeasy.tn", "PERM-001",
                LocalDate.of(1995, 5, 20), LocalDate.now().plusYears(3));
        service.create(req1);

        ClientRequest req2 = sampleRequest("08123456", "karim2@driveeasy.tn", "PERM-002",
                LocalDate.of(1996, 5, 20), LocalDate.now().plusYears(3));
        assertThrows(ConflictException.class, () -> service.create(req2));
    }

    @Test
    void testEligibilite_AgeSuperieurA21EtPermisValide() {
        ClientRequest req = sampleRequest("08123456", "karim@driveeasy.tn", "PERM-001",
                LocalDate.now().minusYears(25), LocalDate.now().plusYears(2));
        ClientResponse res = service.create(req);

        EligibiliteResponse eligibilite = service.eligibilite(res.getId());
        assertTrue(eligibilite.isEligible());
        assertTrue(eligibilite.isPermisValide());
        assertTrue(eligibilite.getAge() >= 21);
    }

    @Test
    void testEligibilite_MineurIneligible() {
        ClientRequest req = sampleRequest("08123456", "jeune@driveeasy.tn", "PERM-001",
                LocalDate.now().minusYears(19), LocalDate.now().plusYears(2));
        ClientResponse res = service.create(req);

        EligibiliteResponse eligibilite = service.eligibilite(res.getId());
        assertFalse(eligibilite.isEligible());
    }

    @Test
    void testEligibilite_PermisExpireIneligible() {
        ClientRequest req = sampleRequest("08123456", "expire@driveeasy.tn", "PERM-001",
                LocalDate.now().minusYears(30), LocalDate.now().minusDays(10));
        ClientResponse res = service.create(req);

        EligibiliteResponse eligibilite = service.eligibilite(res.getId());
        assertFalse(eligibilite.isEligible());
        assertFalse(eligibilite.isPermisValide());
    }
}
