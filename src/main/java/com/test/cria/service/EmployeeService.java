package com.test.cria.service;

import com.test.cria.dto.employee.EmployeeCreateDTO;
import com.test.cria.dto.employee.EmployeeResponseDTO;
import com.test.cria.entity.Employee;
import com.test.cria.entity.Role;
import com.test.cria.entity.User;
import com.test.cria.exception.userExceptions.UserAlreadyExistsException;
import com.test.cria.mapper.EmployeeMapper;
import com.test.cria.repository.EmployeeRepository;
import com.test.cria.repository.RoleRepository;
import com.test.cria.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(UserRepository userRepository, RoleRepository roleRepository, EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, EmployeeMapper employeeMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.employeeMapper = employeeMapper;
    }

    public EmployeeResponseDTO findById(Long id) {
        return new EmployeeResponseDTO(id, "John Doe", "Black White", "john.email@gmail.com", "123456", List.of());
    }

    @Transactional
    public EmployeeResponseDTO create(EmployeeCreateDTO employeeCreateRequest) {
        if (this.userRepository.existsByEmail(employeeCreateRequest.email())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        if (this.employeeRepository.existsByRegistrationNumber(employeeCreateRequest.registrationNumber())) {
            throw new IllegalArgumentException("Registration number already exists");
        }

        Set<Role> roles = employeeCreateRequest.roles().stream()
                .map(userRole -> this.roleRepository.findByRole(userRole)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + userRole)))
                .collect(Collectors.toSet());

        User user = User.builder()
                .firstName(employeeCreateRequest.firstName())
                .lastName(employeeCreateRequest.lastName())
                .email(employeeCreateRequest.email())
                .password(this.passwordEncoder.encode(employeeCreateRequest.password()))
                .roles(roles)
                .build();

        User savedUser = this.userRepository.save(user);

        Employee employee = Employee.builder()
                .registrationNumber(employeeCreateRequest.registrationNumber())
                .user(savedUser)
                .build();

        Employee savedEmployee = this.employeeRepository.save(employee);

        return employeeMapper.toResponseDTO(savedEmployee);
    }
}
