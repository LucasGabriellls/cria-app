package com.test.cria.dto.employee;

import com.test.cria.entity.enums.RoleEnum;

import java.util.List;

public record EmployeeResponseDTO(
        long id,
        String firstName,
        String lastName,
        String email,
        String registrationNumber,
        List<RoleEnum> roles
) {
}
