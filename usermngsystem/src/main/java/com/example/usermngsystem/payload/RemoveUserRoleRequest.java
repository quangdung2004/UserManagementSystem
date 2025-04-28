package com.example.usermngsystem.payload;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RemoveUserRoleRequest {

    @NotBlank(message = "Role name is required")
    private String name;
}
