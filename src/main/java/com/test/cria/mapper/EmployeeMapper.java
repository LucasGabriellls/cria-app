package com.test.cria.mapper;

import com.test.cria.dto.employee.EmployeeResponseDTO;
import com.test.cria.entity.Employee;
import com.test.cria.entity.Role;
import com.test.cria.entity.enums.RoleEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "roles", source = "user.roles")
    EmployeeResponseDTO toResponseDTO(Employee employee);

    default RoleEnum map(Role role) {
        return role != null ? role.getRole() : null;
    }
}
