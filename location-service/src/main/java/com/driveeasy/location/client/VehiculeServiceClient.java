package com.driveeasy.location.client;

import com.driveeasy.location.dto.StatutUpdateRequest;
import com.driveeasy.location.dto.VehiculeSnapshot;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "vehicule-service")
public interface VehiculeServiceClient {

    @GetMapping("/vehicules/{id}")
    VehiculeSnapshot getVehicule(@PathVariable("id") Long id);

    @GetMapping("/vehicules")
    List<VehiculeSnapshot> getVehicules();

    @PatchMapping("/vehicules/{id}/statut")
    VehiculeSnapshot updateStatut(@PathVariable("id") Long id, @RequestBody StatutUpdateRequest request);
}
