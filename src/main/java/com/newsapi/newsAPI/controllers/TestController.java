package com.newsapi.newsAPI.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        return ResponseEntity.ok(Map.of("message", "Public endpoint reachable without API key"));
    }

    @GetMapping("/secured")
    public ResponseEntity<Map<String, String>> securedEndpoint(Authentication authentication) {
        String apiKey = authentication != null ? authentication.getName() : "unknown";
        return ResponseEntity.ok(Map.of(
                "message", "Secured endpoint reached with valid API key",
                "apiKeyUsed", apiKey
        ));
    }
}
