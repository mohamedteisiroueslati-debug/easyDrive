package com.driveeasy.client.web;

import com.driveeasy.client.dto.CategorieUpdateRequest;
import com.driveeasy.client.dto.ClientRequest;
import com.driveeasy.client.dto.ClientResponse;
import com.driveeasy.client.dto.EligibiliteResponse;
import com.driveeasy.client.service.ClientService;
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

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<ClientResponse> search(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String cin,
            @RequestParam(required = false) String email
    ) {
        return clientService.search(nom, cin, email);
    }

    @GetMapping("/{id}")
    public ClientResponse findById(@PathVariable Long id) {
        return clientService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.create(request));
    }

    @PutMapping("/{id}")
    public ClientResponse update(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        return clientService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/eligibilite")
    public EligibiliteResponse eligibilite(@PathVariable Long id) {
        return clientService.eligibilite(id);
    }

    @PatchMapping("/{id}/categorie")
    public ClientResponse updateCategorie(@PathVariable Long id, @Valid @RequestBody CategorieUpdateRequest request) {
        return clientService.updateCategorie(id, request);
    }
}
