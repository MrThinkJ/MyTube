package com.mrthinkj.securityservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegister {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
}
