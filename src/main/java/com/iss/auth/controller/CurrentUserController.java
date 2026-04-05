package com.iss.auth.controller;

import com.iss.auth.dto.AuthResponse;
import com.iss.security.AuthenticatedUserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class CurrentUserController {

    private final AuthenticatedUserMapper authenticatedUserMapper;

    public CurrentUserController(AuthenticatedUserMapper authenticatedUserMapper) {
        this.authenticatedUserMapper = authenticatedUserMapper;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthResponse> currentUser(Authentication authentication) {
        return ResponseEntity.ok(authenticatedUserMapper.toAuthResponse(authentication));
    }
}
