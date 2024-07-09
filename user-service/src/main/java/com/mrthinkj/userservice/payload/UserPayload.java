package com.mrthinkj.userservice.payload;

import lombok.Data;

@Data
public class UserPayload {
    private String usernameOrEmail;
    private String password;
}
