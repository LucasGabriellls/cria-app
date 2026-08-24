package com.test.cria.service;

import com.test.cria.dto.employee.EmployeeCreateDTO;
import com.test.cria.dto.employee.EmployeeResponseDTO;
import com.test.cria.dto.employee.EmployeeUpdateDTO;
import com.test.cria.entity.Employee;
import com.test.cria.entity.Role;
import com.test.cria.entity.User;
import com.test.cria.entity.enums.RoleEnum;
import com.test.cria.exception.employeeExceptions.EmployeeNotFoundException;
import com.test.cria.exception.userExceptions.UserAlreadyExistsException;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
                null,
                "John",
                "Doe",
                "john@example.com",
                "pwd");

        Employee employee = createEmployee(id, "reg123", user);

        EmployeeResponseDTO expectedDto = new EmployeeResponseDTO(
                id,
                "John",
                "Doe",
                "john@example.com",
                "reg123",
                List.of());

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

        Role role = createRole(1L, RoleEnum.ADMIN);
        User savedUser = createUser(10L, "Jane", "Doe", "jane@example.com", "encoded");
        Employee savedEmployee = createEmployee(20L, "reg-01", savedUser);
        EmployeeResponseDTO expectedDto = new EmployeeResponseDTO(
                20L,
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
        when(employeeMapper.toResponseDTO(savedEmployee)).thenReturn(expectedDto);

        EmployeeResponseDTO result = employeeService.create(createDTO);

        assertThat(result).isEqualTo(expectedDto);
        verify(userRepository).existsByEmail(createDTO.email());
        verify(employeeRepository).existsByRegistrationNumber(createDTO.registrationNumber());
        verify(roleRepository).findByRole(RoleEnum.ADMIN);
        verify(passwordEncoder).encode(createDTO.password());
        verify(userRepository).save(any(User.class));
        verify(employeeRepository).save(any(Employee.class));
        verify(employeeMapper).toResponseDTO(savedEmployee);
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
        // Arrange
        Long id = 1L;
        User existingUser = createUser(5L, "Old","Name","old@example.com","oldpwd");
        Employee existingEmployee = createEmployee(id, "old-reg", existingUser);

        EmployeeUpdateDTO updateDTO = new EmployeeUpdateDTO(
                id,
                "New",
                "Name",
                "new@example.com",
                "newpass",
                "new-reg",
                Set.of(RoleEnum.TEACHER)
        );

        Role role = createRole(2L, RoleEnum.TEACHER);
        User savedUser = createUser(5L, "New","Name","new@example.com","encoded");
        Employee savedEmployee = createEmployee(id, "new-reg", savedUser);

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
        User existingUser = createUser(5L, "X","Y","x@y.com","p");
        Employee existingEmployee = createEmployee(id, "r1", existingUser);

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
        User existingUser = createUser(5L, "X", "Y", "old@mail.com", "p");
        Employee existingEmployee = createEmployee(id, "r1", existingUser);

        EmployeeUpdateDTO updateDTO = new EmployeeUpdateDTO(
                id,
                "X",
                "Y",
                "already@mail.com",
                "p",
                "r1",
                Set.of(RoleEnum.TEACHER)
        );

        Role teacherRole = new Role(1L, RoleEnum.TEACHER); // ajuste para como sua entidade Role é instanciada

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(roleRepository.findByRole(RoleEnum.TEACHER)).thenReturn(Optional.of(teacherRole)); // <-- FALTAVA ESTA LINHA
        when(userRepository.existsByEmail(updateDTO.email())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> employeeService.update(updateDTO));

        verify(employeeRepository).findById(id);
        verify(roleRepository).findByRole(RoleEnum.TEACHER);
        verify(userRepository).existsByEmail(updateDTO.email());
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
}
