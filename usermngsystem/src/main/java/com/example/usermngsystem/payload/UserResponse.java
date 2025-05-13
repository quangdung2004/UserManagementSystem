package com.example.usermngsystem.payload;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserResponse {
    private String username;
    private String email;
    private LocalDateTime accountExpiredAt;
    private Set<String> roles;
}
