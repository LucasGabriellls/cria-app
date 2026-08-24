package com.test.cria.dto.employee;

import com.test.cria.entity.enums.RoleEnum;
import jakarta.validation.constraints.*;

import java.util.Set;

public record EmployeeUpdateDTO(
        @NotNull(message = "ID is required")
        @Positive(message = "ID must be greater than zero")
        Long id,

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
