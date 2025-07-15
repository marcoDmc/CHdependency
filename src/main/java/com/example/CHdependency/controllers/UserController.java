package com.example.CHdependency.controllers;

import com.example.CHdependency.configuration.ConfigAuthentication;
import com.example.CHdependency.dto.user.UserDeleteDTO;
import com.example.CHdependency.dto.user.UserPasswordDTO;
import com.example.CHdependency.dto.user.UserRequestDTO;
import com.example.CHdependency.dto.user.UserResponseDTO;
import com.example.CHdependency.services.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequestMapping("/api/v1")
@Tag(name = "User", description = "deals with everything that involves the user since the creation")
@SecurityRequirement(name = ConfigAuthentication.SECURITY)
public class UserController {
    final UserServices userServices;

    UserController(UserServices userService) {
        this.userServices = userService;
    }

    @PatchMapping("/user/password")
    @Operation(summary = "change password", description = "change the user's password")
    @ApiResponse(responseCode = "200", description = "user password changed successfully")
    @ApiResponse(responseCode = "400", description = "something wrong here, the request could not be executed")
    @ApiResponse(responseCode = "401", description = "You do not have permission to access this route")
    @ApiResponse(responseCode = "500", description = "something wrong here server side error")
    public ResponseEntity<String> updatePasswordUser(@RequestBody UserPasswordDTO user) {
        boolean response = userServices.updatePassword(user);
        if (!response) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(200).body("user update successfully");
    }

    @DeleteMapping("/user/delete")
    @Operation(summary = "delete", description = "delete a user")
    @ApiResponse(responseCode = "200", description = "user deleted successfully")
    @ApiResponse(responseCode = "400", description = "something wrong here, the request could not be executed")
    @ApiResponse(responseCode = "401", description = "You do not have permission to access this route")
    @ApiResponse(responseCode = "500", description = "something wrong here server side error")
    public ResponseEntity<String> deleteUser(@RequestBody UserDeleteDTO user){
        boolean response = userServices.delete(user);
        if (!response) return ResponseEntity.status(400).body("something is wrong");
        else return ResponseEntity.status(200).body("user delete successfully");
    }

    @PostMapping("/user/create")
    @Operation(summary = "create", description = "Creates a new user")
    @ApiResponse(responseCode = "201", description = "user created successfully")
    @ApiResponse(responseCode = "400", description = "something wrong here, the request could not be executed")
    @ApiResponse(responseCode = "500", description = "something wrong here server side error")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO user) {
        UserResponseDTO response = userServices.create(user);

        if (response == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST).body("something is wrong");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
