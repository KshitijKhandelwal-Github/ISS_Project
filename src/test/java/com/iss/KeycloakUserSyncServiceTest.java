package com.iss;

import com.iss.auth.service.KeycloakUserSyncService;
import com.iss.model.*;
import com.iss.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeycloakUserSyncServiceTest {

    @Mock private AccountsRepository accountsRepository;
    @Mock private CandidateRepository candidateRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private KeycloakUserSyncService service;

    @Test
    void sync_ShouldCreateAccount_FromJwt_HR() {
        Authentication auth = mock(Authentication.class);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("sub123")
                .claim("email", "test@example.com")
                .claim("name", "Test User")
                .build();

        when(auth.getPrincipal()).thenReturn(jwt);
        when(auth.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_HR")));

        when(accountsRepository.findByKeycloakSubject("sub123")).thenReturn(Optional.empty());
        when(accountsRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        Accounts saved = new Accounts();
        saved.setId(1L);
        saved.setFullName("Test User");

        when(accountsRepository.save(any())).thenReturn(saved);
        when(userRepository.existsById(1L)).thenReturn(false);

        Accounts result = service.sync(auth);

        assertThat(result).isNotNull();
        verify(userRepository).save(any());
    }

    @Test
    void sync_ShouldCreateCandidateProfile_FromOidc() {
        Authentication auth = mock(Authentication.class);
        OidcUser oidcUser = mock(OidcUser.class);

        when(auth.getPrincipal()).thenReturn(oidcUser);

        when(auth.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_CANDIDATE")));

        when(oidcUser.getSubject()).thenReturn("sub123");
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(oidcUser.getFullName()).thenReturn("Test User");

        when(accountsRepository.findByKeycloakSubject("sub123")).thenReturn(Optional.empty());
        when(accountsRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        Accounts saved = new Accounts();
        saved.setId(2L);
        saved.setFullName("Test User");

        when(accountsRepository.save(any())).thenReturn(saved);
        when(candidateRepository.existsById(2L)).thenReturn(false);

        Accounts result = service.sync(auth);

        assertThat(result).isNotNull();
        verify(candidateRepository).save(any());
    }

    @Test
    void sync_ShouldUpdateExistingAccount() {
        Authentication auth = mock(Authentication.class);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("sub123")
                .claim("email", "test@example.com")
                .build();

        Accounts existing = new Accounts();
        existing.setId(1L);

        when(auth.getPrincipal()).thenReturn(jwt);
        when(auth.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_HR")));
        when(accountsRepository.findByKeycloakSubject("sub123"))
                .thenReturn(Optional.of(existing));

        when(accountsRepository.save(any())).thenReturn(existing);
        when(userRepository.existsById(1L)).thenReturn(true);

        Accounts result = service.sync(auth);

        assertThat(result).isEqualTo(existing);
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void sync_ShouldThrowException_WhenUnsupportedPrincipal() {
        Authentication auth = mock(Authentication.class);

        when(auth.getPrincipal()).thenReturn("invalid");

        assertThatThrownBy(() -> service.sync(auth))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported Keycloak principal type");
    }

    @Test
    void sync_ShouldThrowException_WhenRoleIsNull() {
        Authentication auth = mock(Authentication.class);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("sub123")
                .build();

        when(auth.getPrincipal()).thenReturn(jwt);
        when(auth.getAuthorities()).thenReturn(List.of()); // no role

        assertThatThrownBy(() -> service.sync(auth))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Keycloak role is required");
    }

    @Test
    void sync_ShouldThrowException_WhenSubjectIsNull() {
        Authentication auth = mock(Authentication.class);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("") // invalid
                .build();

        when(auth.getPrincipal()).thenReturn(jwt);
        when(auth.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_HR")));

        assertThatThrownBy(() -> service.sync(auth))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Keycloak subject is required");
    }
}