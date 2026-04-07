package com.iss.auth.controller;

import com.iss.auth.dto.AuthResponse;
import com.iss.auth.service.KeycloakUserSyncService;
import com.iss.model.Accounts;
import com.iss.security.AuthenticatedUserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class CurrentUserController {

    private final AuthenticatedUserMapper authenticatedUserMapper;
    private final KeycloakUserSyncService keycloakUserSyncService;

    public CurrentUserController(AuthenticatedUserMapper authenticatedUserMapper,
                                 KeycloakUserSyncService keycloakUserSyncService) {
        this.authenticatedUserMapper = authenticatedUserMapper;
        this.keycloakUserSyncService = keycloakUserSyncService;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthResponse> currentUser(Authentication authentication) {
        AuthResponse response = authenticatedUserMapper.toAuthResponse(authentication);
        if (isKeycloakAuthentication(authentication)) {
            Accounts account = keycloakUserSyncService.sync(authentication);
            response.setUserId(account.getId());
            response.setFullName(account.getFullName());
            response.setEmail(account.getEmail());
            response.setRole(account.getRole().name());
        }
        return ResponseEntity.ok(response);
    }

    private boolean isKeycloakAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof OidcUser) {
            return true;
        }
        if (principal instanceof Jwt jwt && jwt.getIssuer() != null) {
            return jwt.getIssuer().toString().contains("/realms/iss");
        }
        return false;
    }
}
