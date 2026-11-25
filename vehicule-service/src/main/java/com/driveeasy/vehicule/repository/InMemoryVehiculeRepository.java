package com.driveeasy.vehicule.repository;

import com.driveeasy.vehicule.domain.Vehicule;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryVehiculeRepository implements VehiculeRepository {

    private final Map<Long, Vehicule> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public List<Vehicule> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Vehicule> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Vehicule> findByImmatriculation(String immatriculation) {
        return store.values().stream()
                .filter(vehicule -> vehicule.getImmatriculation().equalsIgnoreCase(immatriculation))
                .findFirst();
    }

    @Override
    public Vehicule save(Vehicule vehicule) {
        if (vehicule.getId() == null) {
            vehicule.setId(sequence.incrementAndGet());
        }
        store.put(vehicule.getId(), vehicule);
        return vehicule;
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
