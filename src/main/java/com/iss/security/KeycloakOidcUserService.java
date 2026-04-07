package com.iss.security;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class KeycloakOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService delegate = new OidcUserService();

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = delegate.loadUser(userRequest);
        Set<GrantedAuthority> mappedAuthorities = new LinkedHashSet<>(oidcUser.getAuthorities());
        mappedAuthorities.addAll(extractAuthoritiesFromAccessToken(userRequest.getAccessToken().getTokenValue()));
        String userNameAttributeName = oidcUser.getUserInfo() != null ? "preferred_username" : "sub";
        return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), userNameAttributeName);
    }

    private Collection<? extends GrantedAuthority> extractAuthoritiesFromAccessToken(String tokenValue) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(tokenValue);
            Map<String, Object> claims = signedJWT.getJWTClaimsSet().getClaims();
            Object realmAccessObject = claims.get("realm_access");
            if (!(realmAccessObject instanceof Map<?, ?> realmAccess)) {
                return List.of();
            }
            Object rolesObject = realmAccess.get("roles");
            if (!(rolesObject instanceof List<?> roles)) {
                return List.of();
            }
            List<GrantedAuthority> authorities = new ArrayList<>();
            for (Object role : roles) {
                if (role instanceof String roleName) {
                    authorities.add(new SimpleGrantedAuthority(normalizeRole(roleName)));
                }
            }
            return authorities;
        } catch (ParseException exception) {
            return List.of();
        }
    }

    private String normalizeRole(String role) {
        return role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase().replace('-', '_');
    }
}
