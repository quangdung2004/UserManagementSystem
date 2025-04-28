package com.example.usermngsystem.service;

import com.example.usermngsystem.entity.Role;
import com.example.usermngsystem.entity.User;
import com.example.usermngsystem.payload.*;
import com.example.usermngsystem.repository.RoleRepository;
import com.example.usermngsystem.repository.UserRepository;
import com.example.usermngsystem.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public String updateUserRoles(String username, RoleRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not exist"));
        Role role = roleRepository.findByName(request.getName())
                .orElseThrow(() -> new RuntimeException("Role '" + request.getName() + "' not found"));

        user.getRoles().add(role);
        userRepository.save(user);
        return "Updated roles";
    }

    public String removeUserRole(String username, RemoveUserRoleRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByName(request.getName())
                .orElseThrow(() -> new RuntimeException("Role '" + request.getName() + "' not found"));

        if (!user.getRoles().contains(role)) {
            throw new RuntimeException("User does not have role '" + role.getName() + "'");
        }

        user.getRoles().remove(role);
        userRepository.save(user);
        return "Removed role '" + role.getName() + "' from user '" + user.getUsername() + "'";
    }

    public User getCurrentUser(UserDetailsImpl userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String updateMyInfo(UserDetailsImpl userDetails, UpdateUserRequest request) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return "User info updated successfully";
    }

    public Map<String, Object> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponse> userResponses = userPage.getContent().stream().map(user -> {
            UserResponse dto = new UserResponse();
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setRoles(user.getRoles()
                    .stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
            return dto;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("users", userResponses);
        response.put("currentPage", userPage.getNumber());
        response.put("totalPages", userPage.getTotalPages());
        response.put("totalItems", userPage.getTotalElements());

        return response;
    }

    public String createUserByAdmin(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        Set<String> roleNames = request.getRoles();
        Set<Role> roles = new HashSet<>();

        if (roleNames == null || roleNames.isEmpty()) {
            Role defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Default role USER not found"));
            roles.add(defaultRole);
        } else {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role '" + roleName + "' not found"));
                roles.add(role);
            }
        }

        user.setRoles(roles);
        userRepository.save(user);
        return "User created successfully with roles: " + roles.stream().map(Role::getName).toList();
    }

    public String deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
        return "User '" + username + "' deleted successfully";
    }
}

