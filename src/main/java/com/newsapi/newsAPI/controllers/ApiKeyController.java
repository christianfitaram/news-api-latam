package com.newsapi.newsAPI.controllers;
import com.newsapi.newsAPI.models.*;
import com.newsapi.newsAPI.repositories.ApiKeyRepository;
import com.newsapi.newsAPI.services.request_log.RequestLogService;
import com.newsapi.newsAPI.utils.ApiKeyGenerator;
import com.newsapi.newsAPI.models.requests.GenerateKeyRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(origins = "*")
    public class ApiKeyController {

    private final ApiKeyRepository apiKeyRepository;
    private final RequestLogService requestLogService;

    @Value("${admin.api.key}")
    private String adminApiKey;

    public ApiKeyController(ApiKeyRepository apiKeyRepository,RequestLogService requestLogService) {
        this.apiKeyRepository = apiKeyRepository;
        this.requestLogService = requestLogService;
    }

    @PostMapping("/generate-key")
    public ResponseEntity<?> generateKey(@RequestBody GenerateKeyRequest request) {
        if (!adminApiKey.equals(request.getAdminKey())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Only admins can generate API keys");
        }

        String newKey = ApiKeyGenerator.generateKey();
        ApiKey apiKey = new ApiKey(request.getUser(), newKey, true, request.getCreditsMicros());
        Instant createdAt = request.getCreatedAt() != null
                ? request.getCreatedAt()
                : Instant.now();
        Instant lastChargedAt = request.getLastChargedAt() != null
                ? request.getLastChargedAt()
                : Instant.now();
        apiKey.setCreatedAt(createdAt);
        apiKey.setLastChargedAt(lastChargedAt);
        if (request.getPlan() != null) {
            apiKey.setPlan(request.getPlan());
        }
        apiKeyRepository.save(apiKey);

        return ResponseEntity.ok(apiKey);
    }

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<PaginatedResponse<RequestLog>>> getLogs(
            @RequestParam String apiKey,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String method
    ) {
        PaginatedResponse<RequestLog> logs = requestLogService.getLogsByApiKey(apiKey, page, Optional.ofNullable(method));
        return ResponseEntity.ok(ApiResponse.success("This a list of the metrics of apiKey: "+apiKey, logs));
    }

    @GetMapping("/logs/filtered")
    public ResponseEntity<ApiResponse<RequestLogAnalyticsResponse<RequestLog>>> getLogs(
            @RequestParam String apiKey,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) String method,
            @RequestParam String adminKey
    ) {
        if (!adminApiKey.equals(adminKey)) {
                throw new IllegalArgumentException("Unauthorized: Only admins can access this endpoint");
        }
        var logs = requestLogService.getLogsByApiKey(
                apiKey,
                page,
                Optional.ofNullable(date),
                Optional.ofNullable(success),
                Optional.ofNullable(method));
        return ResponseEntity.ok(ApiResponse.success("Filtered logs for apiKey: " + apiKey, logs));
    }
}
