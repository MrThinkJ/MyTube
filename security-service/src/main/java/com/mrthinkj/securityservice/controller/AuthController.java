package com.mrthinkj.securityservice.controller;

import com.mrthinkj.securityservice.entity.JwtResponse;
import com.mrthinkj.securityservice.entity.UserPayload;
import com.mrthinkj.securityservice.entity.UserRegister;
import com.mrthinkj.securityservice.entity.UserRegisterResponse;
import com.mrthinkj.securityservice.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@RequestBody UserRegister userRegister){
        return new ResponseEntity<>(authService.register(userRegister), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody UserPayload userPayload){
        return ResponseEntity.ok(authService.login(userPayload));
    }
}
