package com.iss.security;

import com.iss.auth.dto.AuthResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class AuthenticatedUserMapper {

    public AuthResponse toAuthResponse(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        AuthResponse response = new AuthResponse();
        response.setAccessToken(null);
        response.setTokenType("Bearer");
        response.setExpiresInSeconds(0);
        response.setRole(resolvePrimaryRole(authentication.getAuthorities()));

        if (principal instanceof Jwt jwt) {
            response.setFullName(jwt.getClaimAsString("full_name"));
            response.setEmail(jwt.getSubject());
            return response;
        }

        if (principal instanceof OidcUser oidcUser) {
            response.setFullName(firstNonBlank(oidcUser.getFullName(), oidcUser.getPreferredUsername(), oidcUser.getEmail()));
            response.setEmail(firstNonBlank(oidcUser.getEmail(), oidcUser.getPreferredUsername(), authentication.getName()));
            return response;
        }

        response.setFullName(authentication.getName());
        response.setEmail(authentication.getName());
        return response;
    }

    private String resolvePrimaryRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(GrantedAuthority::getAuthority).filter(v -> v.startsWith("ROLE_")).findFirst().orElse(null);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
