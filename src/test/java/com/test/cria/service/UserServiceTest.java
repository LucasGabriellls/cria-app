package com.test.cria.service;

import com.test.cria.dto.user.UserCreateRequestDTO;
import com.test.cria.dto.user.UserUpdateRequestDTO;
import com.test.cria.dto.user.UserPageResponseDTO;
import com.test.cria.dto.user.UserResponseDTO;
import com.test.cria.entity.Role;
import com.test.cria.entity.User;
import com.test.cria.entity.enums.RoleEnum;
import com.test.cria.exception.user.UserNotFoundException;
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
        Page<User> pageUser = new PageImpl<>(
                List.of(user),
                pageable,
                1);

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

        Page<User> pageUser = new PageImpl<>(
                List.of(),
                pageable,
                0);

        Mockito.when(userRepository.findAll(pageable)).thenReturn(pageUser);

        UserPageResponseDTO users = userService.findAllPaginated(page, size);

        Assertions.assertEquals(users.users().isEmpty(), true);
    }

    private static User createUserEntity() {
        Role roleEntity = new Role(1L, RoleEnum.DIRETOR);

        return new User(
                1L,
                "User",
                "Test",
                "user.test@email.com",
                "userPassword123",
                null,
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
                Set.of(RoleEnum.DIRETOR)
        );
    }

    private static UserCreateRequestDTO userCreate() {
        return new UserCreateRequestDTO(
                "User",
                "Test",
                "user.test@email.com",
                "userPassword",
                Set.of(RoleEnum.DIRETOR)
        );
    }

    private static UserResponseDTO userResponseTest() {
        return new UserResponseDTO(
                1L,
                "User Test",
                List.of(RoleEnum.DIRETOR)
        );
    }
}