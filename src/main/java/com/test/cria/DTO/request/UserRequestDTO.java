package com.test.cria.DTO.request;

import com.test.cria.entity.enuns.RoleEnum;

import java.util.Set;

public record UserRequestDTO(


        Long id,


        String CPF,
        String password,

        Set<RoleEnum> role
) {
}
