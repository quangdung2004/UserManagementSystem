package com.example.usermngsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "login-logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String ipAddress;
    String userAgent;
    LocalDateTime loginTime;
    boolean success;
    String message;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;
}
