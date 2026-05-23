package com.tnc.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class GatewayController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/test")
    public String test() {
        return restTemplate.getForObject("http://localhost:8081/analyze", String.class);
    }
}
