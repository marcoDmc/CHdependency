package com.example.CHdependency.controllers;

import com.example.CHdependency.dto.meta.DeleteMeta;
import com.example.CHdependency.dto.meta.FindPeriodDTO;
import com.example.CHdependency.dto.meta.MetaDTO;
import com.example.CHdependency.services.MetaServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PostMapping("meta/g/period")
    public ResponseEntity<Object> findPeriod(@RequestBody FindPeriodDTO period){
        Map<String, Object> response = metaServices.findPeriod(period);
        if(response == null) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(200).body(response);
    }
    @DeleteMapping("meta/delete")
    public ResponseEntity<String> deleteMeta(@RequestBody DeleteMeta meta){
        boolean response = metaServices.delete(meta);
        if (!response) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(200).body("delete meta successfully");
    }
}
