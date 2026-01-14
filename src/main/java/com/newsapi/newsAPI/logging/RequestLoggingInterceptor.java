package com.newsapi.newsAPI.logging;

import com.newsapi.newsAPI.components.EndpointPricing;
import com.newsapi.newsAPI.models.ApiKey;
import com.newsapi.newsAPI.models.RequestLog;
import com.newsapi.newsAPI.repositories.ApiKeyRepository;
import com.newsapi.newsAPI.repositories.RequestLogRepository;
import com.newsapi.newsAPI.services.credits.CreditsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private static final int MAX_BODY_CHARS = 10000;

    private final RequestLogRepository logRepository;
    private final EndpointPricing endpointPricing;
    private final CreditsService creditsService;
    private final ApiKeyRepository apiKeyRepository;

    public RequestLoggingInterceptor(
            RequestLogRepository logRepository,
            EndpointPricing endpointPricing,
            CreditsService creditsService,
            ApiKeyRepository apiKeyRepository
    ) {
        this.logRepository = logRepository;
        this.endpointPricing = endpointPricing;
        this.creditsService = creditsService;
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Store the request start time in request attributes
        request.setAttribute("requestStartTime", LocalDateTime.now());

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Resolve endpoint cost
        long costMicros = endpointPricing.resolveCostMicros(method, path);
        request.setAttribute("costMicros", costMicros);

        // ✅ Free endpoints: proceed without charging and without blocking
        if (costMicros <= 0) {
            request.setAttribute("billed", false);
            return true;
        }

        // Extract API key (header first, then query param)
        String apiKeyValue = extractApiKey(request);

        // If missing, let SecurityConfig / ApiKeyAuthenticationFilter handle auth rules
        if (apiKeyValue == null || apiKeyValue.isBlank()) {
            request.setAttribute("billed", false);
            return true;
        }

        // Load key record (so we can do ADMIN bypass safely)
        ApiKey keyRecord = apiKeyRepository.findByKey(apiKeyValue).orElse(null);
        if (keyRecord == null || !keyRecord.isActive()) {
            request.setAttribute("billed", false);
            return true; // auth filter will handle it if this endpoint requires a key
        }

        // ✅ ADMIN bypasses credits entirely
        if ("ADMIN".equalsIgnoreCase(keyRecord.getUser())) {
            request.setAttribute("billed", false);
            request.setAttribute("balanceAfterMicros", keyRecord.getCreditsMicros());
            return true;
        }

        // Debit credits (atomic). Returns updated ApiKey or null if insufficient.
        ApiKey updated = creditsService.debitIfPossible(apiKeyValue, costMicros);

        if (updated == null) {
            response.setStatus(402);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("""
                {"status":"error","message":"Insufficient credits. Please add credits to continue.","data":{}}
            """);
            return false;
        }

        request.setAttribute("billed", true);
        request.setAttribute("balanceAfterMicros", updated.getCreditsMicros());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LocalDateTime startTime = (LocalDateTime) request.getAttribute("requestStartTime");
        LocalDateTime endTime = LocalDateTime.now();

        RequestLog log = new RequestLog();
        log.setTimestamp(startTime != null ? startTime : endTime); // fallback if null
        log.setMethod(request.getMethod());
        log.setPath(request.getRequestURI());
        log.setIp(request.getRemoteAddr());
        log.setApiKey(extractApiKey(request));
        log.setRequestQuery(request.getQueryString());

        Object requestBodyAttr = request.getAttribute(RequestResponseBodyAdvice.ATTR_REQUEST_BODY);
        if (requestBodyAttr instanceof String requestBody) {
            log.setRequestBody(truncateBody(requestBody));
        } else {
            ContentCachingRequestWrapper cachingRequest =
                    WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
            if (cachingRequest != null) {
                byte[] body = cachingRequest.getContentAsByteArray();
                if (body.length > 0) {
                    String encoding = cachingRequest.getCharacterEncoding();
                    Charset charset = encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;
                    log.setRequestBody(truncateBody(new String(body, charset)));
                }
            }
        }

        Object responseBodyAttr = request.getAttribute(RequestResponseBodyAdvice.ATTR_RESPONSE_BODY);
        if (responseBodyAttr instanceof String responseBody) {
            log.setResponseBody(truncateBody(responseBody));
        } else if (shouldLogResponseBody(request, response)) {
            ContentCachingResponseWrapper cachingResponse =
                    WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
            if (cachingResponse != null) {
                byte[] body = cachingResponse.getContentAsByteArray();
                if (body.length > 0) {
                    String encoding = cachingResponse.getCharacterEncoding();
                    Charset charset = encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;
                    log.setResponseBody(truncateBody(new String(body, charset)));
                }
            }
        }

        int status = response.getStatus();
        log.setStatusCode(status);
        log.setSuccess(status >= 200 && status < 300);

        Object costMicrosObj = request.getAttribute("costMicros");
        if (costMicrosObj instanceof Long) {
            log.setCostMicros((Long) costMicrosObj);
        }

        Object billedObj = request.getAttribute("billed");
        if (billedObj instanceof Boolean) {
            log.setBilled((Boolean) billedObj);
        }

        Object balanceAfterObj = request.getAttribute("balanceAfterMicros");
        if (balanceAfterObj instanceof Long) {
            log.setBalanceAfterMicros((Long) balanceAfterObj);
        }

        if (ex != null) {
            log.setErrorMessage(ex.getMessage());
        }

        if (startTime != null) {
            long durationMillis = Duration.between(startTime, endTime).toMillis();
            log.setDurationMillis(durationMillis);
        }

        logRepository.save(log);
    }

    private String extractApiKey(HttpServletRequest request) {
        String headerKey = request.getHeader("X-API-KEY");
        if (headerKey != null && !headerKey.isBlank()) {
            return headerKey;
        }
        return request.getParameter("apiKey");
    }

    private boolean shouldLogResponseBody(HttpServletRequest request, HttpServletResponse response) {
        String method = request.getMethod();
        boolean isWrite = "POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method);
        if (!isWrite) {
            return false;
        }
        String contentType = response.getContentType();
        if (contentType == null) {
            return true;
        }
        return contentType.startsWith(MediaType.APPLICATION_JSON_VALUE) || contentType.startsWith("text/");
    }

    private String truncateBody(String body) {
        if (body.length() <= MAX_BODY_CHARS) {
            return body;
        }
        return body.substring(0, MAX_BODY_CHARS) + "...[truncated]";
    }
}
