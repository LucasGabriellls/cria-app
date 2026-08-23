package com.test.cria.service;

import com.test.cria.dto.employee.EmployeeCreateDTO;
import com.test.cria.dto.employee.EmployeeResponseDTO;
import com.test.cria.entity.Role;
import com.test.cria.entity.User;
import com.test.cria.entity.enums.RoleEnum;
import com.test.cria.exception.userExceptions.UserNotFoundException;
import com.test.cria.repository.RoleRepository;
import com.test.cria.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public EmployeeService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public EmployeeResponseDTO findById(Long id) {
        return new EmployeeResponseDTO(id, "John Doe", "Black White", "john.email@gmail.com", "123456", List.of(RoleEnum.DIRECTOR));
    }

    public EmployeeResponseDTO create(EmployeeCreateDTO employeeCreateRequest) {
        Set<Role> roles = employeeCreateRequest.roles().stream()
                .map(userRole -> this.roleRepository.findByRole(userRole)
                        .orElseThrow(() -> new UserNotFoundException("Role not found: " + userRole)))
                .collect(Collectors.toSet());

        User user = new User(
                employeeCreateRequest.firstName(),
                employeeCreateRequest.lastName(),
                employeeCreateRequest.email(),
                employeeCreateRequest.password(),

                );
        return null;
    }
}
