package com.example.CHdependency.services;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationServices {
    private final JwtServices jwtService;

    AuthenticationServices(JwtServices jwtService) {
        this.jwtService = jwtService;
    }


    @Transactional
    public Map<String, String> authenticateRefreshToken(Authentication authentication, Long id)
    {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", jwtService.generateAccessToken(authentication));
        tokens.put("refresh_token", jwtService.updateRefreshToken(id));

        return tokens;
    }

    @Transactional
    public Map<String, String> authenticateLogin(Authentication authentication, Long id)
    {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", jwtService.generateAccessToken(authentication));
        tokens.put("refresh_token", jwtService.generateRefreshToken(id));

        return tokens;
    }
}
