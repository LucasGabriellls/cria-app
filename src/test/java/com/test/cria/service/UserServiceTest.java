package com.test.cria.service;

import com.test.cria.dto.request.userRequest.UserCreateRequestDTO;
import com.test.cria.dto.request.userRequest.UserUpdateRequestDTO;
import com.test.cria.dto.response.authResponse.AuthenticationResponseDTO;
import com.test.cria.dto.response.userResponse.UserPageResponseDTO;
import com.test.cria.dto.response.userResponse.UserResponseDTO;
import com.test.cria.entity.Role;
import com.test.cria.entity.User;
import com.test.cria.entity.enums.RoleEnum;
import com.test.cria.exception.userExceptions.UserAlreadyExistsException;
import com.test.cria.exception.userExceptions.UserNotFoundException;
import com.test.cria.mapper.UserMapper;
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

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should find a user by ID")
    void findByIdCase1() {
        User user = userTest();

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
        User user = userTest();
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

    @Test
    @DisplayName("Should create a user if no user with the same name exists")
    void createCase1() {
        UserCreateRequestDTO userCreate = userCreate();
        User user = userTest();
        UserResponseDTO userResponse = userResponseTest();

        Mockito.when(userRepository.existsByEmail(userCreate.email())).thenReturn(false);
        Mockito.when(userMapper.toUserEntity(userCreate)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toUserResponseDTO(user)).thenReturn(userResponse);

        UserResponseDTO createUser = userService.create(userCreate);

        Assertions.assertNotNull(createUser);
        Assertions.assertEquals(userResponse, createUser);
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException exception when user already exists")
    void createCase2() {
        UserCreateRequestDTO userCreate = userCreate();

        Mockito.when(userRepository.existsByEmail(userCreate.email())).thenReturn(true);

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.create(userCreate));
    }
    /*
    @Test
    @DisplayName("Should update user getting a UserCreateRequestDTO")
    void updateCase1() {
        UserUpdateRequestDTO userUpdate = userUpdate();
        User user = userTest();
        UserResponseDTO userResponse = userResponseTest();

        Mockito.when(userRepository.findById(userUpdate.id())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toUserResponseDTO(user)).thenReturn(userResponse);

        UserResponseDTO updateUser = userService.update(userUpdate);

        Assertions.assertNotNull(updateUser);
        Assertions.assertEquals(userResponse, updateUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user is not found")
    void updateCase2() {
        UserUpdateRequestDTO userUpdate = userUpdate();

        Mockito.when(userRepository.findById(userUpdate.id())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.update(userUpdate));
    }

    @Test
    @DisplayName("Should delete user when user ID exists")
    void deleteCase1() {
        User user = userTest();

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.delete(user.getId());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(user.getId());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user is not found")
    void deleteCase2() {
        long id = 1L;

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.delete(id));
    }
    */
    private static User userTest() {
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