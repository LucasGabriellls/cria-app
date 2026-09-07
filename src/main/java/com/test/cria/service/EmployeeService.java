package com.test.cria.service;

import com.test.cria.dto.employee.EmployeeCreateDTO;
import com.test.cria.dto.employee.EmployeePageResponseDTO;
import com.test.cria.dto.employee.EmployeeResponseDTO;
import com.test.cria.entity.Employee;
import com.test.cria.entity.Role;
import com.test.cria.entity.User;
import com.test.cria.entity.enums.RoleEnum;
import com.test.cria.exception.employee.EmployeeDeletionException;
import com.test.cria.exception.user.UserAlreadyExistsException;
import com.test.cria.mapper.EmployeeMapper;
import com.test.cria.repository.EmployeeRepository;
import com.test.cria.repository.RoleRepository;
import com.test.cria.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.test.cria.exception.employee.EmployeeNotFoundException;
import com.test.cria.dto.employee.EmployeeUpdateDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
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
        log.debug("Searching for employee with ID: {} in database", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee with ID: {} not found in database", id);
                    return new EmployeeNotFoundException("Employee not found with id: " + id);
                });

        log.debug("Employee with ID: {} found in database", id);

        return employeeMapper.toResponseDTO(employee);
    }

    public EmployeePageResponseDTO findAllPaginated(int page, int size) {
        log.debug("Fetching paginated employees from database [page={}, size={}]", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.findAll(pageable);

        List<EmployeeResponseDTO> employees = employeePage.getContent().stream()
                .map(employeeMapper::toResponseDTO)
                .toList();

        log.debug("Successfully fetched {} employees from database [totalElements={}, totalPages={}]",
                employees.size(), employeePage.getTotalElements(), employeePage.getTotalPages());

        return new EmployeePageResponseDTO(
                employees,
                employeePage.getTotalElements(),
                employeePage.getTotalPages()
        );
    }

    @Transactional
    public EmployeeResponseDTO create(EmployeeCreateDTO request) {
        log.debug("Starting employee creation process for email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Employee creation failed: email {} already exists", request.email());
            throw new UserAlreadyExistsException("Email already exists");
        }

        if (employeeRepository.existsByRegistrationNumber(request.registrationNumber())) {
            log.warn("Employee creation failed: registration number {} already exists", request.registrationNumber());
            throw new IllegalArgumentException("Registration number already exists");
        }

        Set<Role> roles = request.roles().stream()
                .map(userRole -> roleRepository.findByRole(userRole)
                        .orElseThrow(() -> {
                            log.warn("Employee creation failed: Role {} not found", userRole);
                            return new IllegalArgumentException("Role not found: " + userRole);
                        }))
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
        log.debug("Employee saved successfully with ID: {} and registration number: {}",
                savedEmployee.getId(), savedEmployee.getRegistrationNumber());

        return employeeMapper.toResponseDTO(savedEmployee);
    }

    @Transactional
    public EmployeeResponseDTO update(EmployeeUpdateDTO request) {
        log.debug("Starting employee update process for ID: {}", request.id());

        Employee employee = employeeRepository.findById(request.id())
                .orElseThrow(() -> {
                    log.warn("Employee update failed: ID {} not found", request.id());
                    return new EmployeeNotFoundException("Employee not found");
                });


        if (!employee.getRegistrationNumber().equals(request.registrationNumber())
                && employeeRepository.existsByRegistrationNumber(request.registrationNumber())) {
            log.warn("Employee update failed: registration number {} already in use", request.registrationNumber());
            throw new IllegalArgumentException("Registration number already exists");
        }

        User user = employee.getUser();

        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            log.warn("Employee update failed: email {} already in use", request.email());
            throw new UserAlreadyExistsException("Email already exists");
        }

        Set<Role> roles = request.roles().stream()
                .map(userRole -> roleRepository.findByRole(userRole)
                        .orElseThrow(() -> {
                            log.warn("Employee update failed: Role {} not found", userRole);
                            return new IllegalArgumentException("Role not found: " + userRole);
                        }))
                .collect(Collectors.toSet());

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        employee.setRegistrationNumber(request.registrationNumber());
        employee.setUser(savedUser);

        Employee savedEmployee = employeeRepository.save(employee);

        log.debug("Employee updated successfully with ID: {}", savedEmployee.getId());

        return employeeMapper.toResponseDTO(savedEmployee);
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Starting employee deletion process for ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee deletion failed: ID {} not found", id);
                    return new EmployeeNotFoundException("Employee not found with id: " + id);
                });

        try {
            employeeRepository.delete(employee);
            log.debug("Employee deleted successfully with ID: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Employee deletion failed due to database dependencies for ID: {}", id, e);
            throw new EmployeeDeletionException(
                    "Unable to delete employee with id: " + id + ". Employee has dependencies that cannot be removed."
            );
        }
    }

    public EmployeePageResponseDTO listByRole(RoleEnum request) {
        return null;
    }
}
