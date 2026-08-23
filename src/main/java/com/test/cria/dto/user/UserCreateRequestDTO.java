package com.test.cria.dto.user;

import com.test.cria.entity.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UserCreateRequestDTO (
        @NotBlank(message = "Username is required")
        String firstName,

        @NotBlank(message = "Username is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format invalid")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        @NotEmpty(message = "At least one role is required")
        Set<RoleEnum> roles
) {
}
