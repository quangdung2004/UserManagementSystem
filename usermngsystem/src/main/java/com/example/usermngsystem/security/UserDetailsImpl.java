package com.example.usermngsystem.security;

import com.example.usermngsystem.entity.Role;
import com.example.usermngsystem.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {
    private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = user.getRoles();
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
    @Override public boolean isAccountNonExpired()
    { return true; }
    @Override public boolean isAccountNonLocked()
//    { return true; }
    {
        return user.getAccountExpiredAt() == null || user.getAccountExpiredAt().isAfter(LocalDateTime.now());
    }
    @Override public boolean isCredentialsNonExpired()
    { return true; }
    @Override public boolean isEnabled()
    { return true; }
}
