package com.driveeasy.vehicule.repository;

import com.driveeasy.vehicule.domain.Vehicule;

import java.util.List;
import java.util.Optional;

public interface VehiculeRepository {
    List<Vehicule> findAll();

    Optional<Vehicule> findById(Long id);

    Optional<Vehicule> findByImmatriculation(String immatriculation);

    Vehicule save(Vehicule vehicule);

    void deleteById(Long id);
}
