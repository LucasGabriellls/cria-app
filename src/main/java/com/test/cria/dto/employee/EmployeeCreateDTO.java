package com.test.cria.dto.employee;

import com.test.cria.entity.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record EmployeeCreateDTO(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format invalid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must have at least 6 characters")
        String password,

        @NotBlank(message = "Registration number is required")
        String registrationNumber,

        @NotEmpty(message = "At least one role is required")
        Set<RoleEnum> roles
) {
}
