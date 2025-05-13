package com.example.usermngsystem.controller;

import com.example.usermngsystem.entity.LoginLog;
import com.example.usermngsystem.entity.User;
import com.example.usermngsystem.repository.LoginLogRepository;
import com.example.usermngsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LoginLogController {

    private final LoginLogRepository loginLogRepository;
    private final UserRepository userRepository;
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyLoginLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("loginTime").descending());
        Page<LoginLog> logs = loginLogRepository.findByUserOrderByLoginTimeDesc(user, pageable);

        return ResponseEntity.ok(logs);
    }

}
