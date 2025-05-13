package com.example.usermngsystem.service;

import com.example.usermngsystem.entity.LoginLog;
import com.example.usermngsystem.entity.User;
import com.example.usermngsystem.repository.LoginLogRepository;
import com.example.usermngsystem.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginLogService {

    private final LoginLogRepository loginLogRepository;
    private final UserRepository userRepository;

    public void logLoginAttempt(String username, HttpServletRequest request, boolean success) {
        User user = userRepository.findByUsername(username).orElse(null); // null nếu login fail
        String ip = request.getRemoteAddr();
        String agent = request.getHeader("User-Agent");

        LoginLog log = LoginLog.builder()
                .loginTime(LocalDateTime.now())
                .ipAddress(ip)
                .userAgent(agent)
                .success(success)
                .user(user) // có thể null nếu user không tồn tại
                .build();

        loginLogRepository.save(log);
    }
}
