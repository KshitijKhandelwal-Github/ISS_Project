package com.iss.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.auth.service.KeycloakUserSyncService;
import com.iss.model.Accounts;
import com.iss.security.KeycloakJwtGrantedAuthoritiesConverter;
import com.iss.security.KeycloakOidcUserService;
import com.iss.security.RestAccessDeniedHandler;
import com.iss.security.RestAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(name = "app.security.mode", havingValue = "keycloak")
public class KeycloakSecurityConfig {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final KeycloakJwtGrantedAuthoritiesConverter authoritiesConverter;
    private final KeycloakOidcUserService keycloakOidcUserService;

    public KeycloakSecurityConfig(RestAuthenticationEntryPoint authenticationEntryPoint,
                                  RestAccessDeniedHandler accessDeniedHandler,
                                  KeycloakJwtGrantedAuthoritiesConverter authoritiesConverter,
                                  KeycloakOidcUserService keycloakOidcUserService) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authoritiesConverter = authoritiesConverter;
        this.keycloakOidcUserService = keycloakOidcUserService;
    }

    @Bean
    public SecurityFilterChain keycloakSecurityFilterChain(HttpSecurity http,
                                                           OAuth2AuthorizationRequestResolver pkceAuthorizationRequestResolver,
                                                           AuthenticationSuccessHandler keycloakSuccessHandler,
                                                           OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/health", "/actuator/health").permitAll()
                        .requestMatchers("/api/notifications/**").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/logout/success").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint.authorizationRequestResolver(pkceAuthorizationRequestResolver))
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(keycloakOidcUserService))
                        .successHandler(keycloakSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

        return http.build();
    }

    @Bean
    public OAuth2AuthorizationRequestResolver pkceAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver resolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
        return resolver;
    }

    @Bean
    public AuthenticationSuccessHandler keycloakSuccessHandler(OAuth2AuthorizedClientService authorizedClientService,
                                                               KeycloakUserSyncService keycloakUserSyncService,
                                                               ObjectMapper objectMapper) {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException {
                if (!(authentication instanceof OAuth2AuthenticationToken oauth2Authentication)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 authentication was not available");
                    return;
                }

                OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                        oauth2Authentication.getAuthorizedClientRegistrationId(),
                        oauth2Authentication.getName()
                );

                if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Keycloak access token was not available");
                    return;
                }

                OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
                Instant expiresAt = accessToken.getExpiresAt();
                Accounts account = keycloakUserSyncService.sync(authentication);

                Map<String, Object> tokenResponse = new LinkedHashMap<>();
                tokenResponse.put("userId", account.getId());
                tokenResponse.put("accessToken", accessToken.getTokenValue());
                tokenResponse.put("tokenType", accessToken.getTokenType().getValue());
                tokenResponse.put("expiresAt", expiresAt);
                tokenResponse.put("expiresInSeconds", expiresAt == null ? null : Math.max(0, Duration.between(Instant.now(), expiresAt).getSeconds()));
                tokenResponse.put("scopes", accessToken.getScopes());
                tokenResponse.put("fullName", account.getFullName());
                tokenResponse.put("email", account.getEmail());
                tokenResponse.put("role", account.getRole().name());

                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                objectMapper.writeValue(response.getWriter(), tokenResponse);
            }
        };
    }

    @Bean
    public OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        logoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/api/auth/logout/success");
        return logoutSuccessHandler;
    }
}
