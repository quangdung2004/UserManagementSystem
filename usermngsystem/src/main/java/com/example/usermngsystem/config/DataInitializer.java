package com.example.usermngsystem.config;

import com.example.usermngsystem.entity.Role;
import com.example.usermngsystem.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        if(roleRepository.findByName("USER").isEmpty()) {
            roleRepository.save(new Role(null, "USER"));
        }
        if(roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(new Role(null, "ADMIN"));
        }
    }
}
