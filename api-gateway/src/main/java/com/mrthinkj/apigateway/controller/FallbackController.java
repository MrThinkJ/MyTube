package com.mrthinkj.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    @GetMapping("/user-fallback")
    public ResponseEntity<String> userFallback(){
        return new ResponseEntity<>("User service is not available", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/video-fallback")
    public ResponseEntity<String> videoFallback(){
        return new ResponseEntity<>("Video service is not available", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/auth-fallback")
    public ResponseEntity<String> authFallback(){
        return new ResponseEntity<>("Auth service is not available", HttpStatus.BAD_REQUEST);
    }
}
