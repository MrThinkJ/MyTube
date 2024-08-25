package com.mrthinkj.userservice.controller;

import com.mrthinkj.userservice.payload.*;
import com.mrthinkj.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mrthinkj.core.utils.WebUtils.DEFAULT_PAGE_NUM;
import static com.mrthinkj.core.utils.WebUtils.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    UserService userService;
    @GetMapping
    public ResponseEntity<PagedUsersResponse> getUsers(@RequestParam(defaultValue = DEFAULT_PAGE_NUM) int page,
                                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size){
        // TODO: Implement find paged users
        return null;
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/username/{usernameOrEmail}")
    public ResponseEntity<Long> getUserIdByUsername(@PathVariable String usernameOrEmail){
        return ResponseEntity.ok(userService.getUserIdByUsername(usernameOrEmail));
    }

    @PostMapping("/checkLogin")
    public ResponseEntity<Boolean> isValidUserAccount(@RequestBody UserPayload userPayload){
        return ResponseEntity.ok(userService.isValidUserAccount(userPayload));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createNewUser(@RequestBody UserRegisterDTO userDTO){
        return new ResponseEntity<>(userService.createNewUser(userDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUserById(@ModelAttribute UserDTO userDTO, @PathVariable Long id){
        return ResponseEntity.ok(userService.updateUserById(id, userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id){
        userService.deleteUserById(id);
        return ResponseEntity.ok("Deleted user");
    }
}
