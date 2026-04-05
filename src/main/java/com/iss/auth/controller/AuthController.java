package com.iss.auth.controller;

import com.iss.auth.dto.AuthResponse;
import com.iss.auth.dto.LoginRequest;
import com.iss.auth.service.JwtService;
import com.iss.model.Accounts;
import com.iss.repository.AccountsRepository;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@ConditionalOnProperty(name = "app.security.mode", havingValue = "local-jwt", matchIfMissing = true)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AccountsRepository userAccountRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          AccountsRepository userAccountRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userAccountRepository = userAccountRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        Accounts userAccount = userAccountRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user record not found"));
        return ResponseEntity.ok(jwtService.generateToken(userAccount));
    }
}
