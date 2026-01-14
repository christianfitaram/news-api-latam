package com.newsapi.newsAPI.components;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class EndpointPricing {

    private final AntPathMatcher matcher = new AntPathMatcher();

    // Costs in MICRO-EUROS
    private final Map<String, Long> costs = new LinkedHashMap<>() {{
        // Articles
        put("/v1/news/*", 1500L);               // /v1/news/{id}
        put("/v1/news/by-title", 3000L);
        put("/v1/news/by-topic", 2500L);
        put("/v1/news/by-date", 2500L);
        put("/v1/news/top-entities", 3000L);
        put("/v1/news", 2500L);

        // Events (Option B higher)
        put("/v1/events", 3000L);         // /v1/events/lite/{id}
        put("/v1/events/lite", 3000L); 
        
        put("/v1/events/*", 4500L);             // /v1/events/{id}
        put("/v1/events/lite/*", 2000L);         // /v1/events/lite/{id}
        
        put("/v1/events/by-category", 8000L);
        put("/v1/events/lite/by-category", 7000L);
        
        put("/v1/events/by-date", 8000L);
        put("/v1/events/lite/by-date", 7000L);
        
        put("/v1/events/by-status", 8000L);
        put("/v1/events/lite/by-status", 7000L);
        
        // Logs / metrics
        put("/v1/pipeline-logs/*", 2000L);
        put("/v1/pipeline-logs/by-*", 4000L);
        put("/v1/pipeline-logs", 4000L);
        put("/v1/auth/logs/filtered", 4000L);
        put("/v1/auth/logs", 2000L);
    }};

    public boolean isFreePath(String path) {
        return path.startsWith("/v1/health")
                || path.startsWith("/v1/test")
                || path.startsWith("/v1/auth/")
                || path.startsWith("/actuator/");
    }

    public Long resolveCostMicros(String method, String path) {
        if (isFreePath(path)) return 0L;

        // Method-specific overrides for writes:
        if ("POST".equalsIgnoreCase(method) && "/v1/events".equals(path)) return 20000L;
        if ("PUT".equalsIgnoreCase(method) && matcher.match("/v1/events/*", path)) return 15000L;
        if ("DELETE".equalsIgnoreCase(method) && matcher.match("/v1/events/*", path)) return 10000L;

        // Match read routes
        for (Map.Entry<String, Long> e : costs.entrySet()) {
            if (matcher.match(e.getKey(), path)) {
                return e.getValue();
            }
        }
        // Unknown endpoints: choose a conservative default or free
        return 0L;
    }
}
