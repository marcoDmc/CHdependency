package com.example.CHdependency.controllers;


import com.example.CHdependency.dto.user.CrendentialsUserDTO;
import com.example.CHdependency.services.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private")
@Tag(name="Private", description = "route for user authentication")
@SecurityRequirement(name = ConfigAuthentication.SECURITY)
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(AuthenticationService authenticationService, AuthenticationManager authenticationManager) {
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/authenticate")
    @Operation(summary = "authenticate", description = "route for authentication and verification of user credentials")
    @ApiResponse(responseCode = "200", description = "user successfully authenticated")
    @ApiResponse(responseCode = "400", description = "something wrong here, the request could not be executed")
    @ApiResponse(responseCode = "500", description = "something wrong here server side error")
    public String authenticate(@RequestBody CrendentialsUserDTO authentication)
    {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentication.getUsername(), authentication.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return authenticationService.authenticate(auth);
    }
}
