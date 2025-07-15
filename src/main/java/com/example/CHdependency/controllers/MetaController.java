package com.example.CHdependency.controllers;

import com.example.CHdependency.dto.meta.DeleteMetaDTO;
import com.example.CHdependency.dto.meta.FindPeriodDTO;
import com.example.CHdependency.dto.meta.MetaDTO;
import com.example.CHdependency.services.MetaServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name= "Meta", description = "deals with everything that involves a user's goals")
@SecurityRequirement(name = ConfigAuthentication.SECURITY)
public class MetaController {

    private final MetaServices metaServices;


    MetaController(MetaServices metaServices){this.metaServices = metaServices;}


    @PostMapping("/meta/create")
    @Operation(summary = "create", description = "create a new goal")
    @ApiResponse(responseCode = "201", description = "meta created successfully")
    @ApiResponse(responseCode = "400", description = "something wrong here, the request could not be executed")
    @ApiResponse(responseCode = "401", description = "You do not have permission to access this route")
    @ApiResponse(responseCode = "500", description = "something wrong here server side error")
    public ResponseEntity<String> createMeta(@RequestBody MetaDTO meta){
        boolean response  = metaServices.create(meta);
        if (!response) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(201).body("create meta successfully");
    }

    @PostMapping("meta/g/period")
    @Operation(summary = "goal search", description = "seeks a specific goal from a certain period")
    @ApiResponse(responseCode = "200", description = "goal successfully met within the period")
    @ApiResponse(responseCode = "400", description = "something wrong here, the request could not be executed")
    @ApiResponse(responseCode = "401", description = "You do not have permission to access this route")
    @ApiResponse(responseCode = "500", description = "something wrong here server side error")
    public ResponseEntity<Object> findPeriod(@RequestBody FindPeriodDTO period){
        Map<String, Object> response = metaServices.findPeriod(period);
        if(response == null) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(200).body(response);
    }
    @DeleteMapping("meta/delete")
    public ResponseEntity<String> deleteMeta(@RequestBody DeleteMetaDTO meta){
        boolean response = metaServices.delete(meta);
        if (!response) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(200).body("delete meta successfully");
    }
}
