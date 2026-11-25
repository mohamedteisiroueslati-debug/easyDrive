package com.driveeasy.location.web;

import com.driveeasy.location.domain.StatutLocation;
import com.driveeasy.location.dto.DepartRequest;
import com.driveeasy.location.dto.LocationRequest;
import com.driveeasy.location.dto.LocationResponse;
import com.driveeasy.location.dto.PageResponse;
import com.driveeasy.location.dto.RetourRequest;
import com.driveeasy.location.dto.SimulationResponse;
import com.driveeasy.location.dto.StatistiquesResponse;
import com.driveeasy.location.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public PageResponse<LocationResponse> search(
            @RequestParam(required = false) StatutLocation statut,
            @RequestParam(required = false) Long client,
            @RequestParam(required = false) Long vehicule,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return locationService.search(statut, client, vehicule, dateDebut, dateFin, page, size);
    }

    @GetMapping("/statistiques")
    public StatistiquesResponse statistiques() {
        return locationService.statistiques();
    }

    @GetMapping("/clients/{clientId}/actives")
    public boolean hasActiveLocation(@PathVariable Long clientId) {
        return locationService.hasActiveLocation(clientId);
    }

    @GetMapping("/{id}")
    public LocationResponse findById(@PathVariable Long id) {
        return locationService.findById(id);
    }

    @PostMapping
    public ResponseEntity<LocationResponse> create(@Valid @RequestBody LocationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.create(request));
    }

    @PostMapping("/simulation")
    public SimulationResponse simuler(@Valid @RequestBody LocationRequest request) {
        return locationService.simuler(request);
    }

    @PatchMapping("/{id}/depart")
    public LocationResponse depart(@PathVariable Long id, @Valid @RequestBody DepartRequest request) {
        return locationService.depart(id, request);
    }

    @PatchMapping("/{id}/retour")
    public LocationResponse retour(@PathVariable Long id, @Valid @RequestBody RetourRequest request) {
        return locationService.retour(id, request);
    }

    @PatchMapping("/{id}/annulation")
    public LocationResponse annuler(@PathVariable Long id) {
        return locationService.annuler(id);
    }
}
