package com.example.usermngsystem.controller;

import com.example.usermngsystem.entity.Role;
import com.example.usermngsystem.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRole(@PathVariable String name) {
        return ResponseEntity.ok(roleService.deleteRole(name));
    }

    @PostMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRole(@Valid @RequestBody Role role) {
        Role savedRole = roleService.createRole(role);
        return ResponseEntity.ok(savedRole);
    }
}


