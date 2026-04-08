package com.iss;

import com.iss.auth.controller.CurrentUserController;
import com.iss.auth.dto.AuthResponse;
import com.iss.auth.service.KeycloakUserSyncService;
import com.iss.model.Accounts;
import com.iss.model.enums.RoleType;
import com.iss.security.AuthenticatedUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CurrentUserControllerTest {

    private final AuthenticatedUserMapper mapper = mock(AuthenticatedUserMapper.class);
    private final KeycloakUserSyncService syncService = mock(KeycloakUserSyncService.class);

    private final CurrentUserController controller =
            new CurrentUserController(mapper, syncService);

    @Test
    void currentUser_ShouldReturnResponse_WithoutKeycloakSync() {
        Authentication authentication = mock(Authentication.class);

        AuthResponse response = new AuthResponse();
        response.setUserId(1L);

        when(mapper.toAuthResponse(authentication)).thenReturn(response);
        when(authentication.getPrincipal()).thenReturn("simple-user"); // NOT OIDC/JWT

        ResponseEntity<AuthResponse> result = controller.currentUser(authentication);

        assertThat(result.getBody()).isEqualTo(response);
        verify(syncService, never()).sync(any());
    }

    @Test
    void currentUser_ShouldSync_WhenOidcUser() {
        Authentication authentication = mock(Authentication.class);
        OidcUser oidcUser = mock(OidcUser.class);

        AuthResponse response = new AuthResponse();

        Accounts account = new Accounts();
        account.setId(10L);
        account.setFullName("John Doe");
        account.setEmail("john@example.com");
        account.setRole(RoleType.ROLE_HR);

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(mapper.toAuthResponse(authentication)).thenReturn(response);
        when(syncService.sync(authentication)).thenReturn(account);

        ResponseEntity<AuthResponse> result = controller.currentUser(authentication);

        assertThat(result.getBody().getUserId()).isEqualTo(10L);
        assertThat(result.getBody().getFullName()).isEqualTo("John Doe");
        assertThat(result.getBody().getEmail()).isEqualTo("john@example.com");
        assertThat(result.getBody().getRole()).isEqualTo("ROLE_HR");

        verify(syncService, times(1)).sync(authentication);
    }

    @Test
    void currentUser_ShouldSync_WhenJwtIssuerMatches() {
        Authentication authentication = mock(Authentication.class);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuer("http://localhost:8080/realms/iss") // matches condition
                .build();

        AuthResponse response = new AuthResponse();

        Accounts account = new Accounts();
        account.setId(20L);
        account.setFullName("Alice");
        account.setEmail("alice@example.com");
        account.setRole(RoleType.ROLE_TECHNICAL_PANEL);

        when(authentication.getPrincipal()).thenReturn(jwt);
        when(mapper.toAuthResponse(authentication)).thenReturn(response);
        when(syncService.sync(authentication)).thenReturn(account);

        ResponseEntity<AuthResponse> result = controller.currentUser(authentication);

        assertThat(result.getBody().getUserId()).isEqualTo(20L);
        assertThat(result.getBody().getFullName()).isEqualTo("Alice");
        assertThat(result.getBody().getEmail()).isEqualTo("alice@example.com");
        assertThat(result.getBody().getRole()).isEqualTo("ROLE_TECHNICAL_PANEL");

        verify(syncService).sync(authentication);
    }

    @Test
    void currentUser_ShouldNotSync_WhenJwtIssuerDoesNotMatch() {
        Authentication authentication = mock(Authentication.class);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuer("http://other-issuer.com")
                .build();

        AuthResponse response = new AuthResponse();

        when(authentication.getPrincipal()).thenReturn(jwt);
        when(mapper.toAuthResponse(authentication)).thenReturn(response);

        ResponseEntity<AuthResponse> result = controller.currentUser(authentication);

        assertThat(result.getBody()).isEqualTo(response);
        verify(syncService, never()).sync(any());
    }
}