package com.driveeasy.vehicule.web;

import com.driveeasy.vehicule.domain.Carburant;
import com.driveeasy.vehicule.domain.CategorieVehicule;
import com.driveeasy.vehicule.domain.StatutVehicule;
import com.driveeasy.vehicule.dto.StatutUpdateRequest;
import com.driveeasy.vehicule.dto.VehiculeRequest;
import com.driveeasy.vehicule.dto.VehiculeResponse;
import com.driveeasy.vehicule.service.VehiculeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @GetMapping
    public List<VehiculeResponse> findAll(
            @RequestParam(required = false) CategorieVehicule categorie,
            @RequestParam(required = false) Carburant carburant,
            @RequestParam(required = false) StatutVehicule statut,
            @RequestParam(required = false) BigDecimal prixMax
    ) {
        return vehiculeService.findAll(categorie, carburant, statut, prixMax);
    }

    @GetMapping("/disponibles")
    public List<VehiculeResponse> disponibles(
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin
    ) {
        return vehiculeService.findDisponibles();
    }

    @GetMapping("/{id}")
    public VehiculeResponse findById(@PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @PostMapping
    public ResponseEntity<VehiculeResponse> create(@Valid @RequestBody VehiculeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculeService.create(request));
    }

    @PutMapping("/{id}")
    public VehiculeResponse update(@PathVariable Long id, @Valid @RequestBody VehiculeRequest request) {
        return vehiculeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehiculeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/statut")
    public VehiculeResponse updateStatut(@PathVariable Long id, @Valid @RequestBody StatutUpdateRequest request) {
        return vehiculeService.updateStatut(id, request);
    }
}
