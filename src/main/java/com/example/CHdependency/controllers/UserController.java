package com.example.CHdependency.controllers;

import com.example.CHdependency.models.User;
import com.example.CHdependency.services.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    UserServices userServices;

    @PostMapping("/user/create")
    public ResponseEntity<User> CreateUser(){
//        userServices.createUser();
        return new ResponseEntity<>(new User(), HttpStatus.CREATED);
    }
}
