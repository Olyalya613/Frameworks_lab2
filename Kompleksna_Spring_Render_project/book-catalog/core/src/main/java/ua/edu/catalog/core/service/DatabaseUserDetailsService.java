package ua.edu.catalog.core.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ua.edu.catalog.core.port.AccountStore;

public class DatabaseUserDetailsService implements UserDetailsService {
    private final AccountStore accounts;

    public DatabaseUserDetailsService(AccountStore accounts) {
        this.accounts = accounts;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var account = accounts.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown account"));
        return User.withUsername(account.username())
                .password(account.passwordHash())
                .roles(account.role().name())
                .build();
    }
}
