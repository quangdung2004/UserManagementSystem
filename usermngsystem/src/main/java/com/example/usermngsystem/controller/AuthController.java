package com.example.usermngsystem.controller;

import com.example.usermngsystem.payload.LoginRequest;
import com.example.usermngsystem.payload.LoginResponse;
import com.example.usermngsystem.payload.RegisterRequest;
import com.example.usermngsystem.entity.Role;
import com.example.usermngsystem.entity.User;
import com.example.usermngsystem.repository.RoleRepository;
import com.example.usermngsystem.repository.UserRepository;
import com.example.usermngsystem.security.JwtAuthFilter;
import com.example.usermngsystem.security.JwtUtils;
import com.example.usermngsystem.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        String response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String message = authService.logout(request);
        return ResponseEntity.ok(message);
    }

}