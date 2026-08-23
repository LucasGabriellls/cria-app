package com.test.cria.dto.user;

import java.util.List;

public record UserPageResponseDTO(
        List<UserResponseDTO> users,
        long totalElements,
        int totalPages
) {
}
