package com.test.cria.service;

import com.test.cria.dto.user.UserCreateRequestDTO;
import com.test.cria.dto.user.UserUpdateRequestDTO;
import com.test.cria.dto.user.UserPageResponseDTO;
import com.test.cria.dto.user.UserResponseDTO;
import com.test.cria.entity.Role;
import com.test.cria.entity.User;
import com.test.cria.entity.enums.RoleEnum;
import com.test.cria.exception.user.UserAlreadyExistsException;
import com.test.cria.exception.user.UserNotFoundException;
import com.test.cria.mapper.UserMapper;
import com.test.cria.repository.RoleRepository;
import com.test.cria.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should find a user by ID")
    void findByIdCase1() {
        User user = createUserEntity();

        UserResponseDTO userResponse = userResponseTest();

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toUserResponseDTO(user)).thenReturn(userResponse);

        UserResponseDTO findUser = userService.findById(user.getId());

        Assertions.assertNotNull(findUser);
        Assertions.assertEquals(userResponse, findUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user is not found")
    void findByIdCase2() {
        Long id = 1L;

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.findById(id));
    }

    @Test
    @DisplayName("Should return a page of users")
    void listCase1() {
        User user = createUserEntity();
        UserResponseDTO userResponse = userResponseTest();

        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);
        Page<User> pageUser = new PageImpl<>(List.of(user), pageable, 1);

        Mockito.when(userRepository.findAll(pageable)).thenReturn(pageUser);
        Mockito.when(userMapper.toUserResponseDTO(user)).thenReturn(userResponse);

        UserPageResponseDTO users = userService.findAllPaginated(page, size);

        Assertions.assertEquals(userResponse, users.users().get(0));
    }

    @Test
    @DisplayName("Should return an empty page")
    void listCase2() {
        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);

        Page<User> pageUser = new PageImpl<>(List.of(), pageable, 0);

        Mockito.when(userRepository.findAll(pageable)).thenReturn(pageUser);

        UserPageResponseDTO users = userService.findAllPaginated(page, size);

        Assertions.assertEquals(users.users().isEmpty(), true);
    }

    /*
    @Test
    @DisplayName("Should create a user successfully")
    void createCase1() {
        UserCreateRequestDTO userCreate = userCreate();
        User userEntity = createUserEntity();
        UserResponseDTO expectedResponse = userResponseTest();
        Role role = new Role(1L, RoleEnum.DIRECTOR);

        Mockito.when(userRepository.existsByEmail(userCreate.email())).thenReturn(false);
        Mockito.when(userMapper.toUserEntity(userCreate)).thenReturn(userEntity);
        Mockito.when(passwordEncoder.encode(userCreate.password())).thenReturn("encodedPassword123");
        Mockito.when(roleRepository.findByRole(RoleEnum.DIRECTOR)).thenReturn(Optional.of(role));
        Mockito.when(userRepository.save(userEntity)).thenReturn(userEntity);
        Mockito.when(userMapper.toUserResponseDTO(userEntity)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.create(userCreate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse, result);
        Assertions.assertEquals("encodedPassword123", userEntity.getPassword());

        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(userCreate.email());
        Mockito.verify(userMapper, Mockito.times(1)).toUserEntity(userCreate);
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(userCreate.password());
        Mockito.verify(roleRepository, Mockito.times(1)).findByRole(RoleEnum.DIRECTOR);
        Mockito.verify(userRepository, Mockito.times(1)).save(userEntity);
        Mockito.verify(userMapper, Mockito.times(1)).toUserResponseDTO(userEntity);
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when user already exists")
    void createCase2() {
        UserCreateRequestDTO userCreate = userCreate();

        Mockito.when(userRepository.existsByEmail(userCreate.email())).thenReturn(true);

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.create(userCreate));

        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(userCreate.email());
        Mockito.verify(userMapper, Mockito.never()).toUserEntity(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.anyString());
        Mockito.verify(roleRepository, Mockito.never()).findByRole(Mockito.any());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when role is not found during creation")
    void createCase3() {
        UserCreateRequestDTO userCreate = userCreate();
        User userEntity = createUserEntity();

        Mockito.when(userRepository.existsByEmail(userCreate.email())).thenReturn(false);
        Mockito.when(userMapper.toUserEntity(userCreate)).thenReturn(userEntity);
        Mockito.when(passwordEncoder.encode(userCreate.password())).thenReturn("encodedPassword123");
        Mockito.when(roleRepository.findByRole(RoleEnum.DIRECTOR)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.create(userCreate));

        Mockito.verify(roleRepository, Mockito.times(1)).findByRole(RoleEnum.DIRECTOR);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }


     */
    @Test
    @DisplayName("Should update user successfully when data is valid")
    void updateCase1() {
        UserUpdateRequestDTO userUpdate = userUpdate();
        User userEntity = createUserEntity();
        UserResponseDTO expectedResponse = userResponseTest();
        Role role = new Role(1L, RoleEnum.DIRECTOR);

        Mockito.when(userRepository.findById(userUpdate.id())).thenReturn(Optional.of(userEntity));
        Mockito.when(passwordEncoder.encode(userUpdate.password())).thenReturn("encodedPassword123");
        Mockito.when(roleRepository.findByRole(RoleEnum.DIRECTOR)).thenReturn(Optional.of(role));
        Mockito.when(userMapper.toUserResponseDTO(userEntity)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.update(userUpdate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse, result);
        Assertions.assertEquals(userUpdate.firstName(), userEntity.getFirstName());
        Assertions.assertEquals(userUpdate.lastName(), userEntity.getLastName());
        Assertions.assertEquals(userUpdate.email(), userEntity.getEmail());
        Assertions.assertEquals("encodedPassword123", userEntity.getPassword());

        Mockito.verify(userRepository, Mockito.times(1)).findById(userUpdate.id());
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(userUpdate.password());
        Mockito.verify(roleRepository, Mockito.times(1)).findByRole(RoleEnum.DIRECTOR);
        Mockito.verify(userMapper, Mockito.times(1)).toUserResponseDTO(userEntity);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user is not found by ID")
    void updateCase2() {
        UserUpdateRequestDTO userUpdate = userUpdate();

        Mockito.when(userRepository.findById(userUpdate.id())).thenReturn(Optional.empty());

        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.update(userUpdate)
        );

        Assertions.assertEquals("User not found!", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).existsByEmail(Mockito.anyString());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.anyString());
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when new email is already in use")
    void updateCase3() {
        UserUpdateRequestDTO userUpdate = new UserUpdateRequestDTO(
                1L,
                "User",
                "Test",
                "new.email@email.com",
                "userPassword",
                Set.of(RoleEnum.DIRECTOR)
        );
        User userEntity = createUserEntity();

        Mockito.when(userRepository.findById(userUpdate.id())).thenReturn(Optional.of(userEntity));
        Mockito.when(userRepository.existsByEmail(userUpdate.email())).thenReturn(true);

        UserAlreadyExistsException exception = Assertions.assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.update(userUpdate)
        );

        Assertions.assertEquals("Email already in use", exception.getMessage());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.anyString());
        Mockito.verify(roleRepository, Mockito.never()).findByRole(Mockito.any());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when role does not exist in repository")
    void updateCase4() {
        UserUpdateRequestDTO userUpdate = userUpdate();
        User userEntity = createUserEntity();

        Mockito.when(userRepository.findById(userUpdate.id())).thenReturn(Optional.of(userEntity));
        Mockito.when(passwordEncoder.encode(userUpdate.password())).thenReturn("encodedPassword123");
        Mockito.when(roleRepository.findByRole(RoleEnum.DIRECTOR)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.update(userUpdate)
        );

        Mockito.verify(userMapper, Mockito.never()).toUserResponseDTO(Mockito.any());
    }

    @Test
    @DisplayName("Should delete user successfully when user ID exists")
    void deleteCase1() {
        Long id = 1L;

        Mockito.when(userRepository.existsById(id)).thenReturn(true);

        userService.delete(id);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(id);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user is not found")
    void deleteCase2() {
        Long id = 1L;

        Mockito.when(userRepository.existsById(id)).thenReturn(false);

        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.delete(id)
        );

        Assertions.assertEquals("User not found!", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).existsById(id);
        Mockito.verify(userRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }

    private static User createUserEntity() {
        Role roleEntity = new Role(1L, RoleEnum.DIRECTOR);

        return new User(
                1L,                         // id
                "User",                     // firstName
                "Test",                     // lastName
                "user.test@email.com",      // email
                "userPassword123",          // password
                new Date(),                 // activity
                Set.of(roleEntity)
        );
    }

    private static UserUpdateRequestDTO userUpdate() {
        return new UserUpdateRequestDTO(
                1L,
                "User",
                "Test",
                "user.test@email.com",
                "userPassword",
                Set.of(RoleEnum.DIRECTOR)
        );
    }

    private static UserCreateRequestDTO userCreate() {
        return new UserCreateRequestDTO(
                "User",
                "Test",
                "user.test@email.com",
                "userPassword",
                Set.of(RoleEnum.DIRECTOR)
        );
    }

    private static UserResponseDTO userResponseTest() {
        return new UserResponseDTO(
                1L,
                "User Test",
                List.of(RoleEnum.DIRECTOR)
        );
    }


}