package com.example.usermngsystem.payload;

import lombok.Data;
import java.util.Set;

@Data
public class UserResponse {
    private String username;
    private String email;
    private Set<String> roles;
}
