package com.example.CHdependency.services;

import com.example.CHdependency.configuration.ConfigAuthentication;
import com.example.CHdependency.entities.RefreshToken;
import com.example.CHdependency.repositories.RefreshTokenRepository;
import com.example.CHdependency.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JwtServices {
    @Value("${jwt.private.key}")
    private RSAPrivateKey priv;
    private final JwtEncoder jwtEncoder;
    private final ConfigAuthentication configAuthentication;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private JwtDecoder jwtDecoder;

    public JwtServices(JwtEncoder jwtEncoder,
                       ConfigAuthentication configAuthentication,
                       UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository) {
        this.jwtEncoder = jwtEncoder;
        this.configAuthentication = configAuthentication;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    public String generateAccessToken(Authentication authentication) {
        Instant now = Instant.now();
        long expired = 3600L;

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        String scopes = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("CHdependency")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expired))
                .subject(authentication.getName())
                .claim("scope", scopes)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Transactional
    public String updateRefreshToken(Long userId) {
        String jti = UUID.randomUUID().toString();

        Instant now = Instant.now();
        long expirationInSeconds = 8L * 60 * 60; // 8 horas

        if (userId == null) throw new IllegalArgumentException("ID do usuário não pode ser nulo");

        String issuerUrl = "http://localhost:8080/api/v1";
        String subject = String.valueOf(userId);

        var claims = JwtClaimsSet.builder()
                .issuer(issuerUrl)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationInSeconds))
                .subject(subject)
                .claim("token_type", "refresh_token")
                .claim("jti", jti)
                .build();

        String jwtRefreshToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        RefreshToken existingToken = refreshTokenRepository.findByUserId(userId);

        existingToken.setToken(jti);
        existingToken.setExpiryDate(now.plusSeconds(expirationInSeconds));

        refreshTokenRepository.save(existingToken);

        return jwtRefreshToken;
    }

    @Transactional
    public String generateRefreshToken(Long userId) {
        String jti = UUID.randomUUID().toString();

        Instant now = Instant.now();
        long expirationInSeconds = 8L * 60 * 60; // 8 horas

        if (userId == null) throw new IllegalArgumentException("ID do usuário não pode ser nulo");

        String issuerUrl = "http://localhost:8080/api/v1";
        String subject = String.valueOf(userId);

        var claims = JwtClaimsSet.builder()
                .issuer(issuerUrl)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationInSeconds))
                .subject(subject)
                .claim("token_type", "refresh_token")
                .claim("jti", jti)
                .build();

        String jwtRefreshToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        var user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("user not found."));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(jti);
        refreshToken.setExpiryDate(now.plusSeconds(expirationInSeconds));
        refreshToken.setUser(user);

        refreshTokenRepository.save(refreshToken);

        return jwtRefreshToken;
    }


    public boolean refreshTokenIsTokenValid(String token) {
        String issuerUrl = "http://localhost:8080/api/v1";
        try {
            var jwt = jwtDecoder.decode(token);
            String url = String.valueOf(jwt.getIssuer());
            Instant exp = jwt.getExpiresAt();
            System.out.println("Issuer from token: " + url);
            System.out.println("Exp from token: " + exp);
            System.out.println("url from token: " + url.equals(issuerUrl));
            boolean notExpired = exp == null || exp.isAfter(Instant.now());
            System.out.println("notexp from token: " + notExpired);
            return notExpired && url.equals(issuerUrl);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean refreshTokenIsTokenExpired(String token) {
        var jwt = jwtDecoder.decode(token);
        Instant exp = jwt.getExpiresAt();
        return exp != null && exp.isBefore(Instant.now());
    }

    public String extractUsername(String token) {
        var jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    public String extractEmail(String token) {
        var jwt = jwtDecoder.decode(token);
        return jwt.getClaim("email"); // String
    }

    public String refreshTokenExtractJti(String token) {
        var jwt = jwtDecoder.decode(token);
        return jwt.getClaim("jti"); // String
    }

    @Transactional
    public Optional<RefreshToken> findByHashedToken(String token) {
        var jwt = jwtDecoder.decode(token);

        String tokenType = jwt.getClaim("token_type");
        if (!"refresh_token".equals(tokenType)) return Optional.empty();

        String jti = jwt.getClaim("jti");
        Long userId = Long.valueOf(jwt.getSubject());

        var tokensDoUsuario = refreshTokenRepository.findAllByUserId(userId);
        return tokensDoUsuario.stream()
                .filter(rt -> rt.getExpiryDate() != null && rt.getExpiryDate().isAfter(Instant.now()))
                .filter(rt -> configAuthentication.passwordEncoder().matches(jti, rt.getToken()))
                .findFirst();
    }

    @Transactional
    public void refreshTokenDeleteById(Long id) {
        refreshTokenRepository.deleteById(id);
    }

    @Transactional
    public void refreshTokenDeleteByUserId(Long id) {
        refreshTokenRepository.deleteByUserId(id);
    }

    @Transactional
    public Optional<RefreshToken> refreshTokenFindById(Long id) {
        return refreshTokenRepository.findById(id);
    }

    @Transactional
    public RefreshToken refreshTokenFindByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId);
    }

    public Long refreshTokenExtractUserId(String token) {
        var jwt = this.jwtDecoder.decode(token);
        return Long.parseLong(jwt.getSubject());
    }

}
