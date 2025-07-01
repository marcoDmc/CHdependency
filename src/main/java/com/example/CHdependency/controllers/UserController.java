package com.example.CHdependency.controllers;

import com.example.CHdependency.dto.UserRequestDTO;
import com.example.CHdependency.dto.UserResponseDTO;
import com.example.CHdependency.services.UserServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequestMapping("/api/v1")
public class UserController {
    final UserServices userServices;

    UserController(UserServices userService){
        this.userServices = userService;
    }

    @PostMapping("/user/c")
    public UserResponseDTO createUser(@RequestBody UserRequestDTO user){
        return userServices.createUser(user);
    }
}
