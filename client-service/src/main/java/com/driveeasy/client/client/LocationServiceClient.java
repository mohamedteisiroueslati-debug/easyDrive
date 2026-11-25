package com.driveeasy.client.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "location-service")
public interface LocationServiceClient {

    @GetMapping("/locations/clients/{clientId}/actives")
    boolean hasActiveLocation(@PathVariable("clientId") Long clientId);
}
