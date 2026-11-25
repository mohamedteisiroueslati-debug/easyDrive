package com.driveeasy.client.repository;

import com.driveeasy.client.domain.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    List<Client> findAll();

    Optional<Client> findById(Long id);

    Optional<Client> findByCin(String cin);

    Optional<Client> findByEmail(String email);

    Optional<Client> findByNumeroPermis(String numeroPermis);

    Client save(Client client);

    void deleteById(Long id);
}
