package com.test.cria.DTO.request;

public record UserRequestDTO(
        Long id,
        String CPF,
        String password
) {
}
