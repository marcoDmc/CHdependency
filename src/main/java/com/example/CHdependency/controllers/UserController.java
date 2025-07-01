package com.example.CHdependency.controllers;

import com.example.CHdependency.dto.UserDeleteDTO;
import com.example.CHdependency.dto.UserPasswordDTO;
import com.example.CHdependency.dto.UserRequestDTO;
import com.example.CHdependency.dto.UserResponseDTO;
import com.example.CHdependency.services.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @DeleteMapping("/user/delete")
    public ResponseEntity<String> deleteUser(@RequestBody UserDeleteDTO user){
        boolean response = userServices.deleteUser(user);
        if (!response) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(200).body("user delete successfully");
    }

    @PostMapping("/user/create")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO user) {
        UserResponseDTO response = userServices.createUser(user);

        if (response == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST).body("something is wrong");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
