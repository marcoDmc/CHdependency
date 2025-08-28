package com.example.CHdependency.controllers;

import com.example.CHdependency.configuration.ConfigAuthentication;
import com.example.CHdependency.configuration.UserAuthentication;
import com.example.CHdependency.dto.user.*;
import com.example.CHdependency.services.AuthenticationServices;
import com.example.CHdependency.services.JwtServices;
import com.example.CHdependency.services.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Controller
@RestController
@RequestMapping("/api/v1")
@Tag(name = "User", description = "deals with everything that involves the user since the creation")
@SecurityRequirement(name = ConfigAuthentication.SECURITY)
public class UserController {
    private final UserServices userServices;
    private final AuthenticationServices authenticationService;
    private final AuthenticationManager authenticationManager;
    private final JwtServices jwtServices;

    UserController(UserServices userService,
                   AuthenticationServices authenticationService,
                   AuthenticationManager authenticationManager,
                   JwtServices jwtServices) {
        this.userServices = userService;
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
        this.jwtServices = jwtServices;

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
    public ResponseEntity<String> deleteUser(@RequestBody UserDeleteDTO user) {
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

    @PostMapping("/user/login")
    @Operation(summary = "authenticate", description = "route for login of user credentials")
    @ApiResponse(responseCode = "200", description = "user successfully login")
    @ApiResponse(responseCode = "400", description = "something wrong here, the request could not be executed")
    @ApiResponse(responseCode = "500", description = "something wrong here server side error")
    public ResponseEntity<Map<String, String>> userLogin(@RequestBody CrendentialsUserDTO data, HttpServletResponse response) {

        if (data.getUsername().isEmpty() ||
                data.getPassword().isEmpty() ||
                data.getEmail().isEmpty()) return ResponseEntity.ok(Map.of("message", "invalid credentials"));

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(data
                        .getUsername(), data.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserAuthentication userDetails = (UserAuthentication) auth.getPrincipal();
        Long userId = userDetails.getId();

        var token = jwtServices.refreshTokenFindById(userId);
        if (token.isPresent()) {
            jwtServices.refreshTokenDeleteById(userId);
        }

        Map<String, String> tokens = authenticationService.authenticateLogin(auth, userId);

        Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.get("refresh_token"));
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/api/v1/user/refresh");
        refreshTokenCookie.setMaxAge((int) (8 * 60 * 60));

        response.addCookie(refreshTokenCookie);

        String access_token = tokens.get("access_token");
        return ResponseEntity.ok(Map.of("access_token", access_token));

    }
}
