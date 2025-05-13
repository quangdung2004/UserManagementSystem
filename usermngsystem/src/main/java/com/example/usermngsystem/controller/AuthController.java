package com.example.usermngsystem.controller;

import com.example.usermngsystem.entity.LoginLog;
import com.example.usermngsystem.payload.LoginRequest;
import com.example.usermngsystem.payload.LoginResponse;
import com.example.usermngsystem.payload.RegisterRequest;
import com.example.usermngsystem.entity.Role;
import com.example.usermngsystem.entity.User;
import com.example.usermngsystem.repository.LoginLogRepository;
import com.example.usermngsystem.repository.RoleRepository;
import com.example.usermngsystem.repository.UserRepository;
import com.example.usermngsystem.security.JwtAuthFilter;
import com.example.usermngsystem.security.JwtUtils;
import com.example.usermngsystem.service.AuthService;
import com.example.usermngsystem.service.LoginLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @Autowired
    private LoginLogService loginLogService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        String response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            LoginResponse response = authService.login(request);

            // Ghi log đăng nhập thành công
            loginLogService.logLoginAttempt(request.getUsername(), httpRequest, true);

            return ResponseEntity.ok(response);

        } catch (LockedException ex) {
            // Ghi log bị khóa
            loginLogService.logLoginAttempt(request.getUsername(), httpRequest, false);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", ex.getMessage()));

        } catch (BadCredentialsException ex) {
            // Ghi log đăng nhập sai
            loginLogService.logLoginAttempt(request.getUsername(), httpRequest, false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpServletRequest) {
//        LoginResponse response = authService.login(request);
//        loginLogService.logLoginAttempt(request.getUsername(), httpServletRequest, true);
//
//        return ResponseEntity.ok(response);
//    }

    @DeleteMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String message = authService.logout(request);
        return ResponseEntity.ok(message);
    }

}