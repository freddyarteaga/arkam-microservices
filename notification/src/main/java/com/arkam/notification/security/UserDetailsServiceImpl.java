package com.arkam.notification.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // In-memory users for demo. In production, integrate with user-service or database
    private final Map<String, UserDetails> users = Map.of(
            "user", User.withUsername("user")
                    .password("{noop}password") // No encoding for demo
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    .build(),
            "admin", User.withUsername("admin")
                    .password("{noop}password")
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .build()
    );

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }
}