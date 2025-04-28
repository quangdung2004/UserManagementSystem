package com.example.usermngsystem.service;

import com.example.usermngsystem.entity.*;
import com.example.usermngsystem.payload.LoginRequest;
import com.example.usermngsystem.payload.LoginResponse;
import com.example.usermngsystem.payload.RegisterRequest;
import com.example.usermngsystem.repository.*;
import com.example.usermngsystem.security.JwtAuthFilter;
import com.example.usermngsystem.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        user.setRoles(new HashSet<>());
        user.getRoles().add(role);

        userRepository.save(user);
        return "User registered successfully";
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateJwt(authentication.getName());

        return new LoginResponse(token);
    }

    public String logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtAuthFilter.addToBlacklist(token);
            return "User logged out successfully";
        }

        throw new RuntimeException("Authorization token not found");
    }


}
