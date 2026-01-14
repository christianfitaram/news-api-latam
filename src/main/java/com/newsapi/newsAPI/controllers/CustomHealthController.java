package com.newsapi.newsAPI.controllers;

import org.springframework.boot.actuate.health.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/health")
public class CustomHealthController {

    private final HealthEndpoint healthEndpoint;

    public CustomHealthController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        HealthComponent healthComponent = healthEndpoint.health();

        // Status code (UP, DOWN, etc.)
        Status status = healthComponent.getStatus();

        Map<String, Object> details;

        // If it’s a CompositeHealth (default for /health)
        if (healthComponent instanceof CompositeHealth composite) {
            details = composite.getComponents().entrySet().stream()
                    // filter out diskSpace component
                    .filter(entry -> !"diskSpace".equals(entry.getKey()))
                    // map key -> HealthComponent to key -> details map or status
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> {
                                HealthComponent comp = entry.getValue();
                                if (comp instanceof Health h) {
                                    return Map.of(
                                            "status", h.getStatus().getCode(),
                                            "details", h.getDetails()
                                    );
                                } else {
                                    return Map.of("status", comp.getStatus().getCode());
                                }
                            }
                    ));
        } else if (healthComponent instanceof Health health) {
            // fallback if not composite
            details = health.getDetails();
        } else {
            details = Map.of();
        }

        Map<String, Object> response = Map.of(
                "status", status.getCode(),
                "details", details
        );

        return "UP".equals(status.getCode()) ?
                ResponseEntity.ok(response) :
                ResponseEntity.status(503).body(response);
    }
}
