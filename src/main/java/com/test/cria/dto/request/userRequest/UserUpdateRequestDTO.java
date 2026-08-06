package com.test.cria.dto.request.userRequest;

import com.test.cria.entity.enums.RoleEnum;
import jakarta.validation.constraints.*;

import java.util.Set;

public record UserUpdateRequestDTO(

        @NotNull(message = "ID is required")
        @Positive(message = "ID must be greater than zero")
        Long id,

        @NotBlank(message = "Username is required")
        String userName,

        @NotBlank(message = "Password is required")
        String password,

        @NotEmpty(message = "At least one role is required")
        Set<RoleEnum> role
) {
}
