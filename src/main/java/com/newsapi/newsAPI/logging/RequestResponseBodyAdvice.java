package com.newsapi.newsAPI.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Type;

@ControllerAdvice
public class RequestResponseBodyAdvice extends RequestBodyAdviceAdapter implements ResponseBodyAdvice<Object> {
    public static final String ATTR_REQUEST_BODY = "capturedRequestBody";
    public static final String ATTR_RESPONSE_BODY = "capturedResponseBody";

    private final ObjectMapper objectMapper;

    public RequestResponseBodyAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, org.springframework.http.HttpInputMessage inputMessage,
                                MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        if (inputMessage instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest request = servletRequest.getServletRequest();
            if (isWriteMethod(request.getMethod())) {
                request.setAttribute(ATTR_REQUEST_BODY, safeToJson(body));
            }
        }
        return body;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> converterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return null;
        }
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            if (isWriteMethod(httpRequest.getMethod()) && isLoggableMediaType(selectedContentType)) {
                httpRequest.setAttribute(ATTR_RESPONSE_BODY, safeToJson(body));
            }
        }
        return body;
    }

    private boolean isWriteMethod(String method) {
        return "POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method);
    }

    private boolean isLoggableMediaType(MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }
        return MediaType.APPLICATION_JSON.includes(mediaType)
                || MediaType.TEXT_PLAIN.includes(mediaType)
                || MediaType.TEXT_HTML.includes(mediaType);
    }

    private String safeToJson(Object body) {
        try {
            if (body instanceof String str) {
                return str;
            }
            return objectMapper.writeValueAsString(body);
        } catch (Exception ex) {
            return String.valueOf(body);
        }
    }
}
