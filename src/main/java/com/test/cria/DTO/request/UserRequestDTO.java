package com.test.cria.DTO.request;

import com.test.cria.entity.enuns.RoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UserRequestDTO(


        Long id,

        @NotBlank(message = "O nome de usuário é obrigatório")
        String userName,

        @NotBlank(message = "A senha é obrigatória")
        String password,

        @NotEmpty(message = "O usuário precisa ter pelo menos uma role")
        Set<RoleEnum> role
) {
}
