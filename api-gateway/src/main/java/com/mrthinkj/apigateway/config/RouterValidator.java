package com.mrthinkj.apigateway.config;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {
    public static final List<String> OPEN_GET_API_ENDPOINTS = List.of("/api/v1/videos/", "/api/v1/search/", "/api/v1/comments/");
    public static final List<String> OPEN_POST_API_ENDPOINTS = List.of("/api/v1/auth/login",
            "/api/v1/auth/register", "/api/v1/videos/");
    public Predicate<ServerHttpRequest> isSecuredRequest = serverHttpRequest -> {
        List<String> endpoints = serverHttpRequest.getMethod().equals(HttpMethod.GET) ? OPEN_GET_API_ENDPOINTS : OPEN_POST_API_ENDPOINTS;
        return endpoints
                .stream()
                .noneMatch(uri -> serverHttpRequest.getURI().getPath().contains(uri));
    };
}
