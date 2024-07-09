package com.mrthinkj.apigateway.filter;

import com.mrthinkj.apigateway.config.RouterValidator;
import com.mrthinkj.apigateway.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@RefreshScope
@Component
@AllArgsConstructor
public class AuthenticationFilter implements GatewayFilter {
    RouterValidator routerValidator;
    JwtUtils jwtUtils;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (routerValidator.isSecured.test(request)){
            if (isMissingAuthHeader(request)){
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            String jwt = getAuthHeader(request);
            if (!jwtUtils.validateToken(jwt)){
                return onError(exchange, HttpStatus.FORBIDDEN);
            }
            updateInfoOnRequest(exchange, jwt);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private String getAuthHeader(ServerHttpRequest request){
        List<String> auth = request.getHeaders().getOrEmpty("Authorization");
        if (auth.isEmpty())
            return null;
        String token = auth.get(0);
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")){
            return token.substring(7);
        }
        return null;
    }

    private boolean isMissingAuthHeader(ServerHttpRequest request){
        return !request.getHeaders().containsKey("Authorization");
    }

    private void updateInfoOnRequest(ServerWebExchange exchange, String token){
        Claims claims = jwtUtils.getClaimsFromToken(token);
        exchange.getRequest().mutate()
                .header("username", claims.getSubject())
                .build();
    }
}
