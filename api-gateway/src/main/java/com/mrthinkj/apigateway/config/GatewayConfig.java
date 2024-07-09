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
                        predicateSpec -> predicateSpec.path("/api/v1/users/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.filter(authenticationFilter))
                        .uri("lb://user-service"))
                .route("security-service",
                        predicateSpec -> predicateSpec.path("/api/v1/auth/**")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.filter(authenticationFilter))
                                .uri("lb://security-service"))
                .build();
    }

}
