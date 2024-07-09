package com.mrthinkj.securityservice.entity;

import lombok.Data;

@Data
public class UserPayload {
    private String usernameOrEmail;
    private String password;
}
