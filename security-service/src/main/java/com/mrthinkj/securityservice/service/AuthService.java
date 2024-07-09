package com.mrthinkj.securityservice.service;

import com.mrthinkj.securityservice.entity.JwtResponse;
import com.mrthinkj.securityservice.entity.UserPayload;
import com.mrthinkj.securityservice.entity.UserRegister;
import com.mrthinkj.securityservice.entity.UserRegisterResponse;

public interface AuthService {
    UserRegisterResponse register(UserRegister userRegister);
    JwtResponse login(UserPayload userPayload);
}
