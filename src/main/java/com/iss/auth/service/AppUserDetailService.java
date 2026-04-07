package com.iss.auth.service;

import com.iss.model.Accounts;
import com.iss.repository.AccountsRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserDetailService implements UserDetailsService {

    private final AccountsRepository userAccountRepository;

    public AppUserDetailService(AccountsRepository AccountsRepository) {
        this.userAccountRepository = AccountsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Accounts userAccount = userAccountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return new User(
                userAccount.getEmail(),
                userAccount.getPassword(),
                userAccount.getStatus().name().equals("ACTIVE"),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority(userAccount.getRole().name()))
        );
    }
}

