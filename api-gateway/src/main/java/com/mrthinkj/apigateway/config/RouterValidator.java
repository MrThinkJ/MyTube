package com.mrthinkj.apigateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {
    public static final List<String> OPEN_API_ENDPOINTS = List.of("/api/v1/auth/login",
            "/api/v1/auth/register", "/api/v1/videos/");

    public Predicate<ServerHttpRequest> isSecured = serverHttpRequest -> OPEN_API_ENDPOINTS
            .stream()
            .noneMatch(uri -> serverHttpRequest.getURI().getPath().contains(uri));
}
