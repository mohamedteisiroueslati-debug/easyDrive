package com.driveeasy.location.repository;

import com.driveeasy.location.domain.Location;

import java.util.List;
import java.util.Optional;

public interface LocationRepository {
    List<Location> findAll();

    Optional<Location> findById(Long id);

    Location save(Location location);
}
