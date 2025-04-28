package com.example.usermngsystem.controller;

import com.example.usermngsystem.payload.CreateUserRequest;
import com.example.usermngsystem.payload.RemoveUserRoleRequest;
import com.example.usermngsystem.payload.RoleRequest;
import com.example.usermngsystem.payload.UpdateUserRequest;
import com.example.usermngsystem.security.UserDetailsImpl;
import com.example.usermngsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/users/{username}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRoles(@PathVariable String username,
                                             @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(userService.updateUserRoles(username, request));
    }

    @DeleteMapping("/users/{username}/remove_roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeUserRole(@PathVariable String username,
                                            @Valid @RequestBody RemoveUserRoleRequest request) {
        return ResponseEntity.ok(userService.removeUserRole(username, request));
    }

    @GetMapping("/users/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ResponseEntity.ok(userService.getCurrentUser(userDetails));
    }

    @PutMapping("/users/me/edit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMyInfo(@Valid @RequestBody UpdateUserRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ResponseEntity.ok(userService.updateMyInfo(userDetails, request));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @PostMapping("/users/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUserByAdmin(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUserByAdmin(request));
    }

    @DeleteMapping("/users/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.deleteUser(username));
    }
}

