package com.iss.auth.service;

import com.iss.auth.dto.AuthResponse;
import com.iss.model.Accounts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expirationHours;

    public JwtService(JwtEncoder jwtEncoder,
                      @Value("${security.jwt.expiration-hours}") long expirationHours) {
        this.jwtEncoder = jwtEncoder;
        this.expirationHours = expirationHours;
    }

    public AuthResponse generateToken(Accounts userAccount) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(expirationHours * 3600);
        List<String> roles = List.of(userAccount.getRole().name());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("iss")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(userAccount.getEmail())
                .claim("roles", roles)
                .claim("full_name", userAccount.getFullName())
                .build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(claims)
        ).getTokenValue();

        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        response.setTokenType("Bearer");
        response.setExpiresInSeconds(expirationHours * 3600);
        response.setFullName(userAccount.getFullName());
        response.setEmail(userAccount.getEmail());
        response.setRole(userAccount.getRole().name());
        return response;
    }
}
