package com.driveeasy.location.client;

import com.driveeasy.location.dto.CategorieUpdateRequest;
import com.driveeasy.location.dto.ClientSnapshot;
import com.driveeasy.location.dto.EligibiliteSnapshot;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "client-service")
public interface ClientServiceClient {

    @GetMapping("/clients/{id}")
    ClientSnapshot getClient(@PathVariable("id") Long id);

    @GetMapping("/clients/{id}/eligibilite")
    EligibiliteSnapshot getEligibilite(@PathVariable("id") Long id);

    @PatchMapping("/clients/{id}/categorie")
    ClientSnapshot updateCategorie(@PathVariable("id") Long id, @RequestBody CategorieUpdateRequest request);
}
