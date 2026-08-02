package com.test.cria.DTO.request;

import com.test.cria.entity.enuns.RoleEnum;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record UserRequestDTO(


        Long id,

        @NotBlank
        String userName,

        @NotBlank
        String password,

        @NotBlank
        Set<RoleEnum> role
) {
}
