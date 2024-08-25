package com.mrthinkj.apigateway.config;

import com.mrthinkj.apigateway.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableHystrix
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    private final AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder){
        return builder.routes()
                .route("user-service",
                        predicateSpec -> predicateSpec.path("/api/v1/users/**", "/api/v1/subscriptions/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.filter(authenticationFilter))
                        .uri("lb://user-service"))
                .route("security-service",
                        predicateSpec -> predicateSpec.path("/api/v1/auth/**")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.filter(authenticationFilter))
                                .uri("lb://security-service"))
                .route("video-service",
                        predicateSpec -> predicateSpec.path("/api/v1/videos/**")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.filter(authenticationFilter))
                                .uri("lb://video-service"))
                .route("comment-service",
                        predicateSpec -> predicateSpec.path("/api/v1/comments/**")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.filter(authenticationFilter))
                                .uri("lb://comment-service"))
                .route("notification-service",
                        predicateSpec -> predicateSpec.path("/api/v1/notifications/**")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.filter(authenticationFilter))
                                .uri("lb://notification-service"))
                .route("search-service",
                        predicateSpec -> predicateSpec.path("/api/v1/search/**")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.filter(authenticationFilter))
                                .uri("lb://search-service"))
                .build();
    }

}
