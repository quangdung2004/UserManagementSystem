package com.example.usermngsystem.service;

import com.example.usermngsystem.entity.Role;
import com.example.usermngsystem.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public String deleteRole(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role '" + name + "' not found"));
        roleRepository.delete(role);
        return "Role '" + name + "' deleted successfully";
    }

    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Role already exists");
        }

        return roleRepository.save(role);
    }
}
