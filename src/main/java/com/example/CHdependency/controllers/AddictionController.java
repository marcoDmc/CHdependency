package com.example.CHdependency.controllers;

import com.example.CHdependency.dto.addiction.AddictionDTO;
import com.example.CHdependency.dto.addiction.DeleteAddictionDTO;
import com.example.CHdependency.services.AddictionServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "delete", description = "delete a addiction")
    @ApiResponse(responseCode = "200", description = "addiction deleted successfully")
    @ApiResponse(responseCode = "400", description = "something wrong here, the request could not be executed")
    @ApiResponse(responseCode = "401", description = "You do not have permission to access this route")
    @ApiResponse(responseCode = "500", description = "something wrong here server side error")
    public ResponseEntity<String> deleteAddiction(@RequestBody DeleteAddictionDTO meta){
        boolean response = addictionServices.delete(meta);
        if (!response) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(200).body("delete addiction successfully");
    }
}
