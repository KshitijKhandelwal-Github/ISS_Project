package com.iss.auth.controller;

import com.iss.auth.dto.AuthResponse;
import com.iss.auth.dto.LoginRequest;
import com.iss.model.Accounts;
import com.iss.repository.AccountsRepository;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@ConditionalOnProperty(name = "app.security.mode", matchIfMissing = true)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AccountsRepository userAccountRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          AccountsRepository userAccountRepository) {
        this.authenticationManager = authenticationManager;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping
    public ResponseEntity<?> getUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(jwt.getClaims());
    }
}
