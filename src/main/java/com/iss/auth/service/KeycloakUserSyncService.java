package com.iss.auth.service;

import com.iss.model.Accounts;
import com.iss.model.Candidate;
import com.iss.model.Users;
import com.iss.model.enums.RoleType;
import com.iss.model.enums.UserStatus;
import com.iss.repository.AccountsRepository;
import com.iss.repository.CandidateRepository;
import com.iss.repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class KeycloakUserSyncService {

    private static final String KEYCLOAK_MANAGED_PASSWORD = "KEYCLOAK_MANAGED_ACCOUNT";

    private final AccountsRepository accountsRepository;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public KeycloakUserSyncService(AccountsRepository accountsRepository,
                                   CandidateRepository candidateRepository,
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        this.accountsRepository = accountsRepository;
        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Accounts sync(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        RoleType role = resolveRole(authentication.getAuthorities());

        if (principal instanceof Jwt jwt) {
            return sync(
                    jwt.getSubject(),
                    firstNonBlank(jwt.getClaimAsString("email"), jwt.getClaimAsString("preferred_username")),
                    firstNonBlank(jwt.getClaimAsString("name"), jwt.getClaimAsString("preferred_username"), jwt.getClaimAsString("email")),
                    role
            );
        }

        if (principal instanceof OidcUser oidcUser) {
            return sync(
                    oidcUser.getSubject(),
                    firstNonBlank(oidcUser.getEmail(), oidcUser.getPreferredUsername()),
                    firstNonBlank(oidcUser.getFullName(), oidcUser.getPreferredUsername(), oidcUser.getEmail()),
                    role
            );
        }

        throw new IllegalArgumentException("Unsupported Keycloak principal type: " + principal.getClass().getName());
    }

    private Accounts sync(String keycloakSubject, String email, String fullName, RoleType role) {
        if (keycloakSubject == null || keycloakSubject.isBlank()) {
            throw new IllegalArgumentException("Keycloak subject is required to sync account");
        }
        if (role == null) {
            throw new IllegalArgumentException("Keycloak role is required to sync account");
        }

        String normalizedEmail = firstNonBlank(email, keycloakSubject + "@keycloak.local");
        String normalizedFullName = firstNonBlank(fullName, normalizedEmail);

        Accounts account = accountsRepository.findByKeycloakSubject(keycloakSubject)
                .or(() -> accountsRepository.findByEmail(normalizedEmail))
                .orElseGet(Accounts::new);

        if (account.getId() == null) {
            account.setPassword(passwordEncoder.encode(KEYCLOAK_MANAGED_PASSWORD));
        }

        account.setKeycloakSubject(keycloakSubject);
        account.setEmail(normalizedEmail);
        account.setFullName(normalizedFullName);
        account.setRole(role);
        account.setStatus(UserStatus.ACTIVE);
        Accounts savedAccount = accountsRepository.save(account);
        // 2. Populate Profile tables based on role
        if (role == RoleType.ROLE_HR || role == RoleType.ROLE_TECHNICAL_PANEL) {
            // Check if Staff User profile exists (using @MapsId, so ID is same as Account ID)
            if (!userRepository.existsById(savedAccount.getId())) {
                Users staffProfile = Users.builder()
                .accounts(savedAccount) // This links the account and sets the ID via @MapsId
                .fullName(savedAccount.getFullName())
                .role(role)
                .department("Synced from Keycloak")
                .active(true)
                .build();
                userRepository.save(staffProfile);
                }
            } else if (role == RoleType.ROLE_CANDIDATE) {
            // Check if Candidate profile exists
            if (!candidateRepository.existsById(savedAccount.getId())) {
                Candidate candidateProfile = Candidate.builder()
                .accounts(savedAccount) // This links the account and sets the ID via @MapsId
                .name(savedAccount.getFullName())
                .status(com.iss.model.enums.CandidateStatus.SCREENED)
                .isActive(true)
                .build();
                candidateRepository.save(candidateProfile);
                }
            }
        return savedAccount;
    }

    private RoleType resolveRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.equals(RoleType.ROLE_HR.name())
                        || authority.equals(RoleType.ROLE_TECHNICAL_PANEL.name())
                        || authority.equals(RoleType.ROLE_CANDIDATE.name()))
                .findFirst()
                .map(RoleType::valueOf)
                .orElse(null);
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
