package com.driveeasy.client.repository;

import com.driveeasy.client.domain.Client;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryClientRepository implements ClientRepository {

    private final Map<Long, Client> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public List<Client> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Client> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Client> findByCin(String cin) {
        return store.values().stream()
                .filter(client -> client.getCin().equalsIgnoreCase(cin))
                .findFirst();
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        return store.values().stream()
                .filter(client -> client.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public Optional<Client> findByNumeroPermis(String numeroPermis) {
        return store.values().stream()
                .filter(client -> client.getNumeroPermis().equalsIgnoreCase(numeroPermis))
                .findFirst();
    }

    @Override
    public Client save(Client client) {
        if (client.getId() == null) {
            client.setId(sequence.incrementAndGet());
        }
        store.put(client.getId(), client);
        return client;
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
