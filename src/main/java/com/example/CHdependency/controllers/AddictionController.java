package com.example.CHdependency.controllers;

import com.example.CHdependency.dto.addiction.AddictionDTO;
import com.example.CHdependency.services.AddictionServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @DeleteMapping("addiction/delete")
    public ResponseEntity<String> deleteAddiction(@RequestBody DeleteAddictionDTO meta){
        boolean response = addictionServices.delete(meta);
        if (!response) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(200).body("delete addiction successfully");
    }
}
