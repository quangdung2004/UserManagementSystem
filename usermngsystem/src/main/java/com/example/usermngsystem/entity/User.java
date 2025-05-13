package com.example.usermngsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String password;

    @Column(unique = true)
    String email;

    @Column(name = "failed_attempts")
    private Integer failedLoginAttempts;

    @Column(name = "locked_until")
    private LocalDateTime accountLockedUntil;

    @Column(name = "account_expired_at")
    private LocalDateTime accountExpiredAt;

    //Quan hệ nhiều-nhiều với bảng Role
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user-roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> roles = new HashSet<>();
}
