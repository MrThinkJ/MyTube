package com.mrthinkj.securityservice.service;

import com.mrthinkj.securityservice.entity.JwtResponse;
import com.mrthinkj.securityservice.entity.UserPayload;
import com.mrthinkj.securityservice.entity.UserRegister;
import com.mrthinkj.securityservice.entity.UserRegisterResponse;
import com.mrthinkj.securityservice.exception.AuthenticationException;
import com.mrthinkj.securityservice.security.JwtProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import static com.mrthinkj.core.utils.APIUtils.USER_API;

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
                .uri(USER_API)
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
                .uri(USER_API+"/checkLogin")
                .bodyValue(userPayload)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block());
    }
}
