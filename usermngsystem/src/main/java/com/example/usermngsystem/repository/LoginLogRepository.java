package com.example.usermngsystem.repository;

import com.example.usermngsystem.entity.LoginLog;
import com.example.usermngsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    Page<LoginLog> findByUserOrderByLoginTimeDesc(User user, Pageable pageable);
    void deleteByUser(User user);
}
