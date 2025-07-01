package com.example.CHdependency.controllers;

import com.example.CHdependency.mappers.UserMapper;
import com.example.CHdependency.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequestMapping("/api/v1")
public class UserController {
    final UserService userService;
    final UserMapper userMapper;

    UserController(UserServices userService){
        this.userServices = userService;
    }

    @PostMapping("/user/c")
    public UserResponseDTO createUser(@RequestBody UserRequestDTO user){
        return userServices.createUser(user);
    }
}
