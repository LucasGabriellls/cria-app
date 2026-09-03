package com.test.cria.dto.classroomStaff;

import com.test.cria.dto.employee.EmployeeResponseDTO;

import java.time.LocalDate;

public record ClassroomStaffResponseDTO(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        boolean isActive,
        EmployeeResponseDTO employee
) {
}
