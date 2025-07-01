package com.example.CHdependency.controllers;

import com.example.CHdependency.dto.UserRequestDTO;
import com.example.CHdependency.dto.UserResponseDTO;
import com.example.CHdependency.services.UserServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequestMapping("/api/v1")
public class UserController {
    final UserServices userServices;

    UserController(UserServices userService){
        this.userServices = userService;
    }

    @PatchMapping("/user/password")
    public ResponseEntity<String> updatePasswordUser(@RequestBody UserPasswordDTO user) {
        boolean response = userServices.updateUserpassword(user);
        if (!response) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(200).body("user update successfully");
    }

    @PostMapping("/user/create")
    public ResponseEntity<String> createUser(@RequestBody UserRequestDTO user) {
        userServices.createUser(user);
        return ResponseEntity.status(201).body("user created successfully");
    }
}
