package com.test.cria.dto.auth;

public record LoginRequestDTO(
        String email,
        String password
) {
}
