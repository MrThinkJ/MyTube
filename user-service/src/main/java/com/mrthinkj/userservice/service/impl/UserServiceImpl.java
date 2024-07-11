package com.mrthinkj.userservice.service.impl;

import com.mrthinkj.userservice.entity.User;
import com.mrthinkj.userservice.exception.ResourceNotFoundException;
import com.mrthinkj.userservice.payload.UserDTO;
import com.mrthinkj.userservice.payload.UserPayload;
import com.mrthinkj.userservice.payload.UserRegisterDTO;
import com.mrthinkj.userservice.payload.UserResponseDTO;
import com.mrthinkj.userservice.repository.UserRepository;
import com.mrthinkj.userservice.service.StorageService;
import com.mrthinkj.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    StorageService storageService;
    @Override
    public UserResponseDTO createNewUser(UserRegisterDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setProfilePicture(null);
        User newUser = userRepository.save(user);
        return mapToResponseDTO(newUser);
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", "Id", userId.toString())
        );
        return mapToResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUserById(Long userId, UserDTO userDTO) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", "Id", userId.toString())
        );
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(encoder.encode(userDTO.getPassword()));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        String pictureFileName = storageService.saveFile(userDTO.getProfilePicture());
        user.setProfilePicture(pictureFileName);
        User updatedUser = userRepository.save(user);
        return mapToResponseDTO(updatedUser);
    }

    @Override
    public Boolean isValidUserAccount(UserPayload userPayload) {
        User user = userRepository.findByUsernameOrEmail(userPayload.getUsernameOrEmail(), userPayload.getUsernameOrEmail())
                .orElse(null);
        return user != null;
    }

    @Override
    public Long getUserIdByUsername(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElse(null);
        return user == null ? null : user.getId();
    }

    @Override
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", "Id", userId.toString())
        );
        userRepository.delete(user);
    }

    private UserResponseDTO mapToResponseDTO(User user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePicture(user.getProfilePicture())
                .build();
    }
}
