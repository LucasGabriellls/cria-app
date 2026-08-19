package com.test.cria.dto.request.userRequest;

import com.test.cria.entity.enums.RoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UserCreateRequestDTO (
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password,

        @NotEmpty(message = "At least one role is required")
        Set<RoleEnum> role
) {
}
