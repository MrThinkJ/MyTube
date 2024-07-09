package com.mrthinkj.securityservice.service;

import com.mrthinkj.securityservice.entity.JwtResponse;
import com.mrthinkj.securityservice.entity.UserPayload;
import com.mrthinkj.securityservice.entity.UserRegister;
import com.mrthinkj.securityservice.entity.UserRegisterResponse;
import com.mrthinkj.securityservice.exception.AuthenticationException;
import com.mrthinkj.securityservice.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

@Service
public class AuthServiceImpl implements AuthService{
    WebClient.Builder webClientBuilder;
    JwtProvider provider;
    DiscoveryClient discoveryClient;

    public AuthServiceImpl(WebClient.Builder webClientBuilder, JwtProvider provider, DiscoveryClient discoveryClient) {
        this.webClientBuilder = webClientBuilder;
        this.provider = provider;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public UserRegisterResponse register(UserRegister userRegister) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userRegister.setPassword(encoder.encode(userRegister.getPassword()));
        return webClientBuilder.build().post()
                .uri("http://user-service/api/v1/users")
                .bodyValue(userRegister)
                .retrieve()
                .bodyToMono(UserRegisterResponse.class)
                .block();
    }

    @Override
    public JwtResponse login(UserPayload userPayload) {
        if (!isValidAccount(userPayload)){
            throw new AuthenticationException("Username or password incorrect");
        }
        return JwtResponse.builder()
                .token(provider.generateToken(userPayload.getUsernameOrEmail()))
                .build();
    }

    private boolean isValidAccount(UserPayload userPayload){
        return Boolean.TRUE.equals(webClientBuilder.build().post()
                .uri("http://USER-SERVICE/api/v1/users/checkLogin")
                .bodyValue(userPayload)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block());
    }
}
