package com.test.cria.dto.user;

import com.test.cria.entity.enums.RoleEnum;

import java.util.List;

public record UserResponseDTO(
        long id,
        String username,
        List<RoleEnum> roles
) {
}
