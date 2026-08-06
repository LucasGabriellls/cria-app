package com.test.cria.dto.response.userResponse;

import com.test.cria.entity.enums.RoleEnum;

import java.util.List;

public record UserResponseDTO(
        long id,
        String userName,
        List<RoleEnum> role
) {
}
