package com.mrthinkj.userservice.service;

import com.mrthinkj.userservice.payload.UserDTO;
import com.mrthinkj.userservice.payload.UserPayload;
import com.mrthinkj.userservice.payload.UserRegisterDTO;
import com.mrthinkj.userservice.payload.UserResponseDTO;

public interface UserService {
    UserResponseDTO createNewUser(UserRegisterDTO userDTO);
    UserResponseDTO getUserById(Long userId);
    UserResponseDTO updateUserById(Long userId, UserDTO userDTO);
    Boolean isValidUserAccount(UserPayload userPayload);
    void deleteUserById(Long userId);
}
