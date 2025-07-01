package com.example.CHdependency.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AddictionController {
    private final AddictionServices addictionServices;

    AddictionController(AddictionServices addictionServices){
        this.addictionServices = addictionServices;
    }

    @PostMapping("/addiction/create")
    public ResponseEntity<String> createAddiction(@RequestBody AddictionDTO addiction){
        boolean response = addictionServices.create(addiction);
        if (!response) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(201).body("create a new addiction type successfully");
    }
}
