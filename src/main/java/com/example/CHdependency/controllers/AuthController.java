package com.example.CHdependency.controllers;


import com.example.CHdependency.configuration.ConfigAuthentication;
import com.example.CHdependency.configuration.UserAuthentication;
import com.example.CHdependency.repositories.UserRepository;
import com.example.CHdependency.services.JwtServices;
import com.example.CHdependency.utils.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Controller
@RestController
@RequestMapping("/api/v1")
@Tag(name = "User", description = "deals with everything that involves the user since the creation")
@SecurityRequirement(name = ConfigAuthentication.SECURITY)
public class AuthController {
    private final JwtServices jwtServices;
    private final UserRepository userRepository;
    private final ConfigAuthentication configAuthentication;
    private final AuthenticationManager authenticationManager;

    AuthController(JwtServices jwtServices,
                   UserRepository userRepository,
                   AuthenticationManager authenticationManager,
                   ConfigAuthentication configAuthentication) {
        this.jwtServices = jwtServices;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.configAuthentication = configAuthentication;
    }

    @PostMapping("/user/refresh")
    @Operation(
            summary = "Refresh Token",
            description = "Rota que utiliza o refresh token armazenado no cookie HttpOnly (`refreshToken`) para gerar um novo par de access token e refresh token.",
            security = @SecurityRequirement(name = "refreshTokenCookie"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tokens gerados com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Refresh token ausente ou inválido"),
                    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
            }
    )
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshTokenFromCookie, HttpServletResponse response) {

        try {

            if (refreshTokenFromCookie == null || refreshTokenFromCookie.isEmpty()) {
                System.out.println("Cookie vazio ou nulo");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token invalid.");
            }

            if (!jwtServices.refreshTokenIsTokenValid(refreshTokenFromCookie)) {
                System.out.println("Validação JWT falhou");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token invalid.");
            }


            String jtiFromJwt = jwtServices.refreshTokenExtractJti(refreshTokenFromCookie);
            System.out.println("JTI: " + jtiFromJwt);
            Long userId = jwtServices.refreshTokenExtractUserId(refreshTokenFromCookie);

            var tokenDatabase = jwtServices.refreshTokenFindByUserId(userId);
            var compareJti = jtiFromJwt.equals(tokenDatabase.getToken());

            if (!compareJti) {
                System.out.println("JTI do banco não bate com o do token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token invalid.");
            }

            var user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User credentials not provided"));

            UserAuthentication userDetails = new UserAuthentication(user);
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());

            String newAccessToken = jwtServices.generateAccessToken(auth);
            String newJwtRefreshToken = jwtServices.updateRefreshToken(userId);

            Cookie newRefreshTokenCookie = new Cookie("refreshToken", newJwtRefreshToken);
            newRefreshTokenCookie.setHttpOnly(true);
            newRefreshTokenCookie.setSecure(true);
            newRefreshTokenCookie.setPath("/api/v1/auth");
            newRefreshTokenCookie.setMaxAge(8 * 60 * 60);
            response.addCookie(newRefreshTokenCookie);


            return ResponseEntity.ok(new TokenResponse(newAccessToken, newJwtRefreshToken));

        } catch (Exception e) {
            System.err.println("Exceção ao processar o Refresh Token: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro ao processar o Refresh Token.");
        }
    }

}
