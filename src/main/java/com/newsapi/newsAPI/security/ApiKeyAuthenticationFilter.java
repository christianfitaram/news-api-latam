package com.newsapi.newsAPI.security;

import com.newsapi.newsAPI.repositories.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyAuthenticationFilter(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = resolveApiKey(request);

        if (StringUtils.hasText(apiKey)) {
            boolean valid = apiKeyRepository.findByKeyAndActiveTrue(apiKey).isPresent();
            if (valid) {
                ApiKeyAuthenticationToken authentication = new ApiKeyAuthenticationToken(apiKey);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Missing or invalid API key");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveApiKey(HttpServletRequest request) {
        String headerKey = request.getHeader("X-API-KEY");
        if (StringUtils.hasText(headerKey)) {
            return headerKey;
        }
        String paramKey = request.getParameter("apiKey");
        if (StringUtils.hasText(paramKey)) {
            return paramKey;
        }
        return null;
    }
}
