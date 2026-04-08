package com.iss;

import com.iss.auth.service.AppUserDetailService;
import com.iss.model.Accounts;
import com.iss.model.enums.RoleType;
import com.iss.model.enums.UserStatus;
import com.iss.repository.AccountsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class AppUserDetailServiceTest {

    @Mock
    private AccountsRepository accountsRepository;

    @InjectMocks
    private AppUserDetailService appUserDetailService;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserIsActive() {
        Accounts account = new Accounts();
        account.setEmail("test@example.com");
        account.setPassword("password");
        account.setStatus(UserStatus.ACTIVE);
        account.setRole(RoleType.ROLE_HR);

        when(accountsRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(account));

        UserDetails userDetails = appUserDetailService.loadUserByUsername("test@example.com");

        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_HR");
    }

    @Test
    void loadUserByUsername_ShouldReturnDisabledUser_WhenNotActive() {
        Accounts account = new Accounts();
        account.setEmail("test@example.com");
        account.setPassword("password");
        account.setStatus(UserStatus.INACTIVE);
        account.setRole(RoleType.ROLE_TECHNICAL_PANEL);

        when(accountsRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(account));

        UserDetails userDetails = appUserDetailService.loadUserByUsername("test@example.com");

        assertThat(userDetails.isEnabled()).isFalse();
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_TECHNICAL_PANEL");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(accountsRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                appUserDetailService.loadUserByUsername("missing@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email");
    }
}