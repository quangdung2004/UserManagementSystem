package com.example.usermngsystem.service;

import com.example.usermngsystem.entity.*;
import com.example.usermngsystem.exception.DuplicatedResourceException;
import com.example.usermngsystem.exception.ResourceNotFoundException;
import com.example.usermngsystem.payload.LoginRequest;
import com.example.usermngsystem.payload.LoginResponse;
import com.example.usermngsystem.payload.RegisterRequest;
import com.example.usermngsystem.repository.*;
import com.example.usermngsystem.security.JwtAuthFilter;
import com.example.usermngsystem.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
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

    @Autowired
    private LoginLogRepository loginLogRepository;

    public String register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicatedResourceException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicatedResourceException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setAccountExpiredAt(LocalDateTime.now().plusYears(1)); // Gia hạn 1 năm
        user.setAccountLockedUntil(null);
        user.setFailedLoginAttempts(0);

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role USER not found"));

        user.setRoles(Set.of(role));
        userRepository.save(user);

        return "User registered successfully";
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        // Check account lock
        if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            throw new LockedException("Account is locked until " + user.getAccountLockedUntil());
        }

        // Check account expired
        if (user.getAccountExpiredAt() != null && user.getAccountExpiredAt().isBefore(LocalDateTime.now())) {
            throw new AccountExpiredException("Account is expired since " + user.getAccountExpiredAt());
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Reset login attempt
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            userRepository.save(user);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtils.generateJwt(authentication.getName());

            return new LoginResponse(token);

        } catch (BadCredentialsException ex) {
            int newAttempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(newAttempts);

            if (newAttempts >= 5) {
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(10));
            }

            userRepository.save(user);

            throw new BadCredentialsException("Invalid credentials. Attempts: " + newAttempts +
                    (newAttempts >= 5 ? " - Account locked for 10 minutes." : ""));
        }
    }

//    public String register(RegisterRequest request) {
//
//        if (userRepository.existsByUsername(request.getUsername())) {
//            throw new RuntimeException("Username already exists");
//        }
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        User user = new User();
//        user.setUsername(request.getUsername());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setEmail(request.getEmail());
//        user.setAccountExpiredAt(LocalDateTime.now().plusYears(1));
//        //user.setAccountExpiredAt(LocalDateTime.now().plusMinutes(5));
//
//        Role role = roleRepository.findByName("USER")
//                .orElseThrow(() -> new RuntimeException("Role USER not found"));
//
//        user.setRoles(new HashSet<>());
//        user.getRoles().add(role);
//
//        userRepository.save(user);
//        return "User registered successfully";
//    }
//
//
//    public LoginResponse login(LoginRequest request) {
//
//        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
//
//        if (optionalUser.isEmpty()) {
//            // Vẫn throw ra ngoài để controller xử lý và ghi log
//            throw new BadCredentialsException("Invalid credentials");
//        }
//
//        User user = optionalUser.get();
//
//        if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
//            throw new LockedException("Account is locked until " + user.getAccountLockedUntil());
//        }
//
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            request.getUsername(),
//                            request.getPassword()
//                    )
//            );
//
//            // Reset số lần đăng nhập sai
//            user.setFailedLoginAttempts(0);
//            user.setAccountLockedUntil(null);
//            userRepository.save(user);
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            String token = jwtUtils.generateJwt(authentication.getName());
//
//            return new LoginResponse(token);
//
//        } catch (BadCredentialsException ex) {
//            int newAttempts = user.getFailedLoginAttempts() + 1;
//            user.setFailedLoginAttempts(newAttempts);
//
//            if (newAttempts >= 5) {
//                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(10));
//            }
//
//            userRepository.save(user);
//
//            throw new BadCredentialsException("Invalid credentials. Attempts: " + newAttempts +
//                    (newAttempts >= 5 ? " - Account locked for 10 minutes." : ""));
//        }
//    }


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
