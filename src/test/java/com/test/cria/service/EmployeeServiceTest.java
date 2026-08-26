package com.test.cria.service;

import com.test.cria.dto.employee.EmployeeCreateDTO;
import com.test.cria.dto.employee.EmployeePageResponseDTO;
import com.test.cria.dto.employee.EmployeeResponseDTO;
import com.test.cria.dto.employee.EmployeeUpdateDTO;
import com.test.cria.entity.Employee;
import com.test.cria.entity.Role;
import com.test.cria.entity.User;
import com.test.cria.entity.enums.RoleEnum;
import com.test.cria.exception.employee.EmployeeNotFoundException;
import com.test.cria.exception.user.UserAlreadyExistsException;
import com.test.cria.mapper.EmployeeMapper;
import com.test.cria.repository.EmployeeRepository;
import com.test.cria.repository.RoleRepository;
import com.test.cria.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    @DisplayName("Should return EmployeeDTO when searching by existing ID")
    void shouldReturnEmployeeDTOWhenSearchingByExistingID() {
        Long id = 1L;

        User user = createUser(
                id,
                "John",
                "Doe",
                "john@example.com",
                "pwd");

        Employee employee = createEmployee(
                id,
                "123",
                user);

        EmployeeResponseDTO expectedDto = new EmployeeResponseDTO(
                id,
                "John",
                "Doe",
                "john@example.com",
                "123",
                List.of(RoleEnum.TEACHER));

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeMapper.toResponseDTO(employee)).thenReturn(expectedDto);

        EmployeeResponseDTO result = employeeService.findById(id);

        assertThat(result).isEqualTo(expectedDto);
        verify(employeeRepository).findById(id);
        verify(employeeMapper).toResponseDTO(employee);
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when searching by non-existing ID")
    void shouldThrowEmployeeNotFoundExceptionWhenSearchingByNonExistingID() {
        Long id = 99L;

        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.findById(id));
        verify(employeeRepository).findById(id);
        verifyNoMoreInteractions(employeeMapper);
    }

    @Test
    @DisplayName("should return paginated employee page response DTO when records exist")
    void shouldReturnPaginatedEmployeePageResponseDTOWhenRecordsExist() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        User user = createUser(
                1L,
                "John",
                "Doe",
                "john.doe@email.com",
                "encoded");

        Employee employee = createEmployee(
                1L,
                "123",
                user);

        EmployeeResponseDTO employeeResponseDTO = createEmployeeResponseDTO(
                1L,
                "John",
                "Doe",
                "john.doe@email.com",
                "123");

        Page<Employee> employeePage = new PageImpl<>(
                List.of(employee),
                pageable,
                1L);

        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
        when(employeeMapper.toResponseDTO(employee)).thenReturn(employeeResponseDTO);

        EmployeePageResponseDTO result = employeeService.findAllPaginated(page, size);

        assertThat(result).isNotNull();
        assertThat(result.employees()).hasSize(1);
        assertThat(result.employees().get(0).id()).isEqualTo(1L);
        assertThat(result.employees().get(0).firstName()).isEqualTo("John");
        assertThat(result.employees().get(0).lastName()).isEqualTo("Doe");
        assertThat(result.employees().get(0).email()).isEqualTo("john.doe@email.com");
        assertThat(result.employees().get(0).registrationNumber()).isEqualTo("123");
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.totalPages()).isEqualTo(1);

        verify(employeeRepository).findAll(pageable);
        verify(employeeMapper).toResponseDTO(employee);
        verifyNoMoreInteractions(employeeRepository, employeeMapper);
    }

    @Test
    @DisplayName("should return empty employee page response DTO when no records exist")
    void shouldReturnEmptyEmployeePageResponseDTOWhenNoRecordsExist() {
        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);

        Page<Employee> emptyEmployeePage = new PageImpl<>(
                Collections.emptyList(),
                pageable,
                0L);

        when(employeeRepository.findAll(pageable)).thenReturn(emptyEmployeePage);

        EmployeePageResponseDTO result = employeeService.findAllPaginated(page, size);

        assertThat(result).isNotNull();
        assertThat(result.employees()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0L);
        assertThat(result.totalPages()).isEqualTo(0);

        verify(employeeRepository).findAll(pageable);
        verifyNoInteractions(employeeMapper);
    }



    @Test
    @DisplayName("Should create Employee when data is valid")
    void shouldCreateEmployeeWhenDataIsValid() {
        EmployeeCreateDTO createDTO = new EmployeeCreateDTO(
                "Jane",
                "Doe",
                "jane@example.com",
                "secret",
                "reg-01",
                Set.of(RoleEnum.ADMIN)
        );

        Role role = createRole(
                1L,
                RoleEnum.ADMIN);

        User user = createUser(
                null,
                "Jane",
                "Doe",
                "jane@example.com",
                "encoded"
        );

        User savedUser = createUser(
                10L,
                "Jane",
                "Doe",
                "jane@example.com",
                "encoded");

        Employee employee = createEmployee(
                null,
                "reg-01",
                user);

        Employee savedEmployee = createEmployee(
                10L,
                "reg-01",
                savedUser);

        EmployeeResponseDTO expectedDto = new EmployeeResponseDTO(
                10L,
                "Jane",
                "Doe",
                "jane@example.com",
                "reg-01",
                List.of(RoleEnum.ADMIN));

        when(userRepository.existsByEmail(createDTO.email())).thenReturn(false);
        when(employeeRepository.existsByRegistrationNumber(createDTO.registrationNumber())).thenReturn(false);
        when(roleRepository.findByRole(RoleEnum.ADMIN)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(createDTO.password())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        when(employeeMapper.toResponseDTO(any(Employee.class))).thenReturn(expectedDto);

        EmployeeResponseDTO result = employeeService.create(createDTO);

        assertThat(result).isEqualTo(expectedDto);
        verify(userRepository).existsByEmail(createDTO.email());
        verify(employeeRepository).existsByRegistrationNumber(createDTO.registrationNumber());
        verify(roleRepository).findByRole(RoleEnum.ADMIN);
        verify(passwordEncoder).encode(createDTO.password());
        verify(userRepository).save(any(User.class));
        verify(employeeRepository).save(any(Employee.class));
        verify(employeeMapper).toResponseDTO(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email already exists on creation")
    void shouldThrowUserAlreadyExistsExceptionWhenEmailAlreadyExistsOnCreation() {
        EmployeeCreateDTO createDTO = new EmployeeCreateDTO(
                "A",
                "B",
                "dup@example.com",
                "pwd",
                "r1",
                Set.of(RoleEnum.ADMIN)
        );

        when(userRepository.existsByEmail(createDTO.email())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> employeeService.create(createDTO));
        verify(userRepository).existsByEmail(createDTO.email());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when registration number already exists on creation")
    void shouldThrowIllegalArgumentExceptionWhenRegistrationNumberAlreadyExistsOnCreation() {
        EmployeeCreateDTO createDTO = new EmployeeCreateDTO(
                "A",
                "B",
                "a@example.com",
                "pwd",
                "r1",
                Set.of(RoleEnum.ADMIN)
        );

        when(userRepository.existsByEmail(createDTO.email())).thenReturn(false);
        when(employeeRepository.existsByRegistrationNumber(createDTO.registrationNumber())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> employeeService.create(createDTO));
        verify(userRepository).existsByEmail(createDTO.email());
        verify(employeeRepository).existsByRegistrationNumber(createDTO.registrationNumber());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when role is not found on creation")
    void shouldThrowIllegalArgumentExceptionWhenRoleNotFoundOnCreation() {
        EmployeeCreateDTO createDTO = new EmployeeCreateDTO(
                "A",
                "B",
                "a@example.com",
                "pwd",
                "r1",
                Set.of(RoleEnum.ADMIN)
        );

        when(userRepository.existsByEmail(createDTO.email())).thenReturn(false);
        when(employeeRepository.existsByRegistrationNumber(createDTO.registrationNumber())).thenReturn(false);
        when(roleRepository.findByRole(RoleEnum.ADMIN)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> employeeService.create(createDTO));
        verify(roleRepository).findByRole(RoleEnum.ADMIN);
    }

    @Test
    @DisplayName("Should update Employee when data is valid")
    void shouldUpdateEmployeeWhenDataIsValid() {
        Long id = 1L;

        User existingUser = createUser(
                id,
                "Old",
                "Name",
                "old@example.com",
                "oldpwd");

        Employee existingEmployee = createEmployee(
                id,
                    "123",
                existingUser);

        EmployeeUpdateDTO updateDTO = new EmployeeUpdateDTO(
                id,
                "New",
                "Name",
                "new@example.com",
                "newpass",
                "new-reg",
                Set.of(RoleEnum.TEACHER)
        );

        Role role = createRole(
                2L,
                RoleEnum.TEACHER);

        User savedUser = createUser(
                5L,
                "New",
                "Name",
                "new@example.com",
                "encoded");

        Employee savedEmployee = createEmployee(
                id,
                "new-reg",
                savedUser);

        EmployeeResponseDTO expectedDto = new EmployeeResponseDTO(
                id,
                "New",
                "Name",
                "new@example.com",
                "new-reg",
                java.util.List.of(RoleEnum.TEACHER)
        );

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.existsByRegistrationNumber(updateDTO.registrationNumber())).thenReturn(false);
        when(userRepository.existsByEmail(updateDTO.email())).thenReturn(false);
        when(roleRepository.findByRole(RoleEnum.TEACHER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(updateDTO.password())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        when(employeeMapper.toResponseDTO(savedEmployee)).thenReturn(expectedDto);

        EmployeeResponseDTO result = employeeService.update(updateDTO);

        assertThat(result).isEqualTo(expectedDto);
        verify(employeeRepository).findById(id);
        verify(passwordEncoder).encode(updateDTO.password());
        verify(userRepository).save(any(User.class));
        verify(employeeRepository).save(any(Employee.class));
        verify(employeeMapper).toResponseDTO(savedEmployee);
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when updating non-existent employee")
    void shouldThrowEmployeeNotFoundExceptionWhenUpdatingNonExistentEmployee() {
        Long id = 99L;

        EmployeeUpdateDTO updateDTO = new EmployeeUpdateDTO(
                id,
                "A",
                "B",
                "a@b.com",
                "p",
                "r",
                Set.of(RoleEnum.TEACHER)
        );

        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.update(updateDTO));
        verify(employeeRepository).findById(id);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when registration number already exists while updating")
    void shouldThrowIllegalArgumentExceptionWhenRegistrationNumberAlreadyExistsWhileUpdating() {
        Long id = 1L;

        User existingUser = createUser(
                5L,
                "X",
                "Y",
                "x@y.com",
                "p");

        Employee existingEmployee = createEmployee(
                id,
                "r1",
                existingUser);

        EmployeeUpdateDTO updateDTO = new EmployeeUpdateDTO(
                id,
                "X",
                "Y",
                "x@y.com",
                "p",
                "r2",
                Set.of(RoleEnum.TEACHER)
        );

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.existsByRegistrationNumber(updateDTO.registrationNumber())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> employeeService.update(updateDTO));
        verify(employeeRepository).findById(id);
        verify(employeeRepository).existsByRegistrationNumber(updateDTO.registrationNumber());
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email already exists while updating")
    void shouldThrowUserAlreadyExistsExceptionWhenEmailAlreadyExistsWhileUpdating() {
        Long id = 1L;

        User existingUser = createUser(
                5L,
                "X",
                "Y",
                "old@mail.com",
                "p");

        Employee existingEmployee = createEmployee(
                id,
                "r1",
                existingUser);

        EmployeeUpdateDTO updateDTO = new EmployeeUpdateDTO(
                id,
                "X",
                "Y",
                "already@mail.com",
                "p",
                "r1",
                Set.of(RoleEnum.TEACHER)
        );

        Role teacherRole = new Role(
                1L,
                RoleEnum.TEACHER);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(roleRepository.findByRole(RoleEnum.TEACHER)).thenReturn(Optional.of(teacherRole));
        when(userRepository.existsByEmail(updateDTO.email())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> employeeService.update(updateDTO));

        verify(employeeRepository).findById(id);
        verify(roleRepository).findByRole(RoleEnum.TEACHER);
        verify(userRepository).existsByEmail(updateDTO.email());
    }

    @Test
    @DisplayName("should delete employee when id exists")
    void shouldDeleteEmployeeWhenIdExists() {
        Long id = 1L;

        User user = createUser(
                id,
                "John",
                "Doe",
                "john.doe@email.com",
                "pswd");

        Employee employee = createEmployee(
                id,
                "123",
                user);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        employeeService.delete(id);

        verify(employeeRepository).findById(id);
        verify(employeeRepository).delete(employee);
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    @DisplayName("should throw EmployeeNotFoundException when deleting non-existent employee")
    void shouldThrowEmployeeNotFoundExceptionWhenDeletingNonExistentEmployee() {
        Long nonExistentId = 999L;

        when(employeeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.delete(nonExistentId)
        );

        assertEquals("Employee not found with id: " + nonExistentId, exception.getMessage());

        verify(employeeRepository).findById(nonExistentId);
        verify(employeeRepository, never()).delete(any());
        verifyNoMoreInteractions(employeeRepository);
    }

    private Role createRole(Long id, RoleEnum roleEnum) {
        Role role = new Role();
        role.setId(id);
        role.setRole(roleEnum);
        return role;
    }

    private User createUser(Long id, String firstName, String lastName, String email, String password) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    private Employee createEmployee(Long id, String registrationNumber, User user) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setRegistrationNumber(registrationNumber);
        employee.setUser(user);
        return employee;
    }

    private EmployeeResponseDTO createEmployeeResponseDTO(Long id, String firstName, String lastName,
                                                          String email, String registrationNumber) {
        return new EmployeeResponseDTO(
                id,
                firstName,
                lastName,
                email,
                registrationNumber,
                List.of(RoleEnum.TEACHER)
        );
    }
}
