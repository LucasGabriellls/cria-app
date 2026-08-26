package com.test.cria.service;

import com.test.cria.dto.employee.EmployeeCreateDTO;
import com.test.cria.dto.employee.EmployeePageResponseDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.test.cria.exception.employeeExceptions.EmployeeNotFoundException;
import com.test.cria.dto.employee.EmployeeUpdateDTO;

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

    public EmployeeService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            EmployeeMapper employeeMapper
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.employeeMapper = employeeMapper;
    }


    public EmployeeResponseDTO findById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        return employeeMapper.toResponseDTO(employee);
    }

    public EmployeePageResponseDTO findAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.findAll(pageable);

        List<EmployeeResponseDTO> employees = employeePage
                .map(employeeMapper::toResponseDTO)
                .toList();

        return new EmployeePageResponseDTO(
                employees,
                employeePage.getTotalElements(),
                employeePage.getTotalPages()
        );
    }

    @Transactional
    public EmployeeResponseDTO create(EmployeeCreateDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        if (employeeRepository.existsByRegistrationNumber(request.registrationNumber())) {
            throw new IllegalArgumentException("Registration number already exists");
        }

        Set<Role> roles = request.roles().stream()
                .map(userRole -> roleRepository.findByRole(userRole)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + userRole)))
                .collect(Collectors.toSet());

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);

        Employee employee = Employee.builder()
                .registrationNumber(request.registrationNumber())
                .user(savedUser)
                .build();

        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponseDTO(savedEmployee);
    }

    @Transactional
    public EmployeeResponseDTO update(EmployeeUpdateDTO request) {
        Employee employee = this.employeeRepository.findById(request.id())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        if (!employee.getRegistrationNumber().equals(request.registrationNumber())
                && this.employeeRepository.existsByRegistrationNumber(request.registrationNumber())) {
            throw new IllegalArgumentException("Registration number already exists");
        }

        Set<Role> roles = request.roles().stream()
                .map(userRole -> this.roleRepository.findByRole(userRole)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + userRole)))
                .collect(Collectors.toSet());

        User user = employee.getUser();

        if (!user.getEmail().equals(request.email()) && this.userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(this.passwordEncoder.encode(request.password()));
        user.setRoles(roles);

        User savedUser = this.userRepository.save(user);

        employee.setRegistrationNumber(request.registrationNumber());
        employee.setUser(savedUser);

        Employee savedEmployee = this.employeeRepository.save(employee);

        return this.employeeMapper.toResponseDTO(savedEmployee);
    }
}
