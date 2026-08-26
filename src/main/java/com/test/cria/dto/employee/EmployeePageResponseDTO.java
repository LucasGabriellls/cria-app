package com.test.cria.dto.employee;

import java.util.List;

public record EmployeePageResponseDTO(
        List<EmployeeResponseDTO> employees,
        long totalElements,
        int totalPages
) {
}
