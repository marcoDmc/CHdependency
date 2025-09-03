package com.example.CHdependency.controllers;

import com.example.CHdependency.configuration.ConfigAuthentication;
import com.example.CHdependency.dto.goal.GoalDeleteDTO;
import com.example.CHdependency.dto.goal.GoalDTO;
import com.example.CHdependency.dto.goal.GoalFindPeriodDTO;
import com.example.CHdependency.services.GoalServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name= "Goal", description = "deals with everything that involves a user's goals")
@SecurityRequirement(name = ConfigAuthentication.SECURITY)
public class GoalController {

    private final GoalServices goalServices;


    GoalController(GoalServices goalServices){this.goalServices = goalServices;}


    @PostMapping("/goal/create")
    @Operation(summary = "create", description = "create a new goal")
    @ApiResponse(responseCode = "201", description = "goal created successfully")
    @ApiResponse(responseCode = "400", description = "something wrong here, the request could not be executed")
    @ApiResponse(responseCode = "401", description = "You do not have permission to access this route")
    @ApiResponse(responseCode = "500", description = "something wrong here server side error")
    public ResponseEntity<String> createGoal(@RequestBody GoalDTO goal){
        boolean response  = goalServices.create(goal);
        if (!response) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(201).body("create goal successfully");
    }

    @PostMapping("goal/find/period")
    @Operation(summary = "goal search", description = "seeks a specific goal from a certain period")
    @ApiResponse(responseCode = "200", description = "goal successfully met within the period")
    @ApiResponse(responseCode = "400", description = "something wrong here, the request could not be executed")
    @ApiResponse(responseCode = "401", description = "You do not have permission to access this route")
    @ApiResponse(responseCode = "500", description = "something wrong here server side error")
    public ResponseEntity<Object> findGoalPeriod(@RequestBody GoalFindPeriodDTO period){
        Map<String, Object> response = goalServices.findPeriod(period);
        if(response == null) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(200).body(response);
    }
    @DeleteMapping("goal/delete")
    @Operation(summary = "delete", description = "delete a goal")
    @ApiResponse(responseCode = "200", description = "goal deleted successfully")
    @ApiResponse(responseCode = "400", description = "something wrong here, the request could not be executed")
    @ApiResponse(responseCode = "401", description = "You do not have permission to access this route")
    @ApiResponse(responseCode = "500", description = "something wrong here server side error")
    public ResponseEntity<String> deleteGoal(@RequestBody GoalDeleteDTO goal){
        boolean response = goalServices.delete(goal);
        if (!response) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(200).body("delete goal successfully");
    }
}
