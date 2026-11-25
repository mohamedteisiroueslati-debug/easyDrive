package com.driveeasy.location.repository;

import com.driveeasy.location.domain.Location;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryLocationRepository implements LocationRepository {

    private final Map<Long, Location> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public List<Location> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Location> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Location save(Location location) {
        if (location.getId() == null) {
            location.setId(sequence.incrementAndGet());
        }
        store.put(location.getId(), location);
        return location;
    }
}
