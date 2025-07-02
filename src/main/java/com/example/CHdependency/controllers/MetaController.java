package com.example.CHdependency.controllers;

import com.example.CHdependency.dto.meta.MetaDTO;
import com.example.CHdependency.services.MetaServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MetaController {

    private final MetaServices metaServices;


    MetaController(MetaServices metaServices){this.metaServices = metaServices;}


    @PostMapping("/meta/create")
    public ResponseEntity<String> createMeta(@RequestBody MetaDTO meta){
        boolean response  = metaServices.create(meta);
        if (!response) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(201).body("create meta successfully");
    }
}
