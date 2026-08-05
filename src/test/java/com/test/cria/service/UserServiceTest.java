package com.test.cria.service;

import com.test.cria.DTO.request.UserRequestDTO;
import com.test.cria.DTO.response.UserResponseDTO;
import com.test.cria.entity.User;
import com.test.cria.entity.enuns.RoleEnum;
import com.test.cria.exception.userExceptions.InvalidAttributeException;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should find user by ID")
    void findByIdCase1() {
        Long userId = 1L;
        String userName = "Lucas Gabriel";

        User user = new  User(
                userId,
                userName,
                "Lucasdev123",
                Set.of(
                        RoleEnum.GESTOR,
                        RoleEnum.PROFESSOR,
                        RoleEnum.RESPONSAVEL
                )
        );

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                userId,
                userName
        );

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toUserResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO responseDTO = userService.findById(userId);


        Assertions.assertNotNull(responseDTO);
        Assertions.assertEquals(userResponseDTO, responseDTO);
    }

    @Test
    @DisplayName("Should throw InvalidAttributeException when user ID is less than 1")
    void findByIdCase2() {
        Assertions.assertThrows(InvalidAttributeException.class, () -> userService.findById(0L));

    }

    @Test
    @DisplayName("Should throw InvalidAttributeException when the user is not found")
    void findByIdCase3() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(InvalidAttributeException.class, () -> userService.findById(1L));
    }

    @Test
    @DisplayName("Should return a list of users")
    void listCase1() {

        int page = 0;
        int size = 10;

        long userId = 1L;
        String userName = "Lucas Gabriel";


        User user = new User(
                userId,
                userName,
                "LucasDev123",
                Set.of(
                        RoleEnum.GESTOR
                )
        );

        Pageable pageable = PageRequest.of(page, size);

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                userId,
                userName
        );

        List<User> userList = List.of(user);

        Page<User> pageUser = new PageImpl<>(
                userList,
                pageable,
                userList.size()
        );

        Mockito.when(userRepository.findAll(pageable)).thenReturn(pageUser);
        Mockito.when(userMapper.toUserResponseDTO(user)).thenReturn(userResponseDTO);

        Page<UserResponseDTO> pageUserResponse = userService.list(page, size);

        Assertions.assertNotNull(pageUserResponse);
    }

    @Test
    @DisplayName("Should return the created user")
    void createCase1() {
        Long userId = 1L;
        String userName = "Lucas Gabriel";

        User user = new  User(
                userId,
                userName,
                "Lucasdev123",
                Set.of(
                        RoleEnum.GESTOR,
                        RoleEnum.PROFESSOR,
                        RoleEnum.RESPONSAVEL
                )
        );

        UserRequestDTO userRequestDTO = new UserRequestDTO(
                userId,
                userName,
                "Lucasdev123",
                Set.of(
                        RoleEnum.GESTOR,
                        RoleEnum.PROFESSOR,
                        RoleEnum.RESPONSAVEL
                )
        );

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                userId,
                userName
        );

        Mockito.when(userMapper.toUserEntity((userRequestDTO))).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toUserResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO userResponse = userService.create(userRequestDTO);

        Assertions.assertNotNull(userResponse);
        Assertions.assertEquals(userResponseDTO, userResponse);
    }

    @Test
    @DisplayName("Should update a user")
    void updateCase1() {
        Long userId = 1L;
        String userName = "Lucas Gabriel";

        User user = new  User(
                userId,
                userName,
                "Lucasdev123",
                Set.of(
                        RoleEnum.GESTOR,
                        RoleEnum.PROFESSOR,
                        RoleEnum.RESPONSAVEL
                )
        );

        UserRequestDTO userRequest = new  UserRequestDTO(
                userId,
                userName,
                "Lucasdev123",
                Set.of(
                        RoleEnum.GESTOR,
                        RoleEnum.PROFESSOR,
                        RoleEnum.RESPONSAVEL
                )
        );

        UserResponseDTO userResponse = new UserResponseDTO(
                userId,
                userName
        );

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toUserResponseDTO(user)).thenReturn(userResponse);

        UserResponseDTO userResult = userService.update(userRequest);

        Assertions.assertNotNull(userResult);
        Assertions.assertEquals(userResponse, userResult);
    }

    @Test
    @DisplayName("Should throw InvalidAttributeException when the user ID is less than 1")
    void updateCase2() {
        UserRequestDTO userRequest = new  UserRequestDTO(
                -1L,
                "Lucas Gabriel",
                "Lucasdev123",
                Set.of(
                        RoleEnum.GESTOR,
                        RoleEnum.PROFESSOR,
                        RoleEnum.RESPONSAVEL
                )
        );

        Assertions.assertThrows(InvalidAttributeException.class, () -> userService.update(userRequest));
    }

    @Test
    @DisplayName("Should throw InvalidAttributeException when the user is not found")
    void updateCase3() {
        UserRequestDTO userRequest = new  UserRequestDTO(
                1L,
                "Lucas Gabriel",
                "Lucasdev123",
                Set.of(
                        RoleEnum.GESTOR,
                        RoleEnum.PROFESSOR,
                        RoleEnum.RESPONSAVEL
                )
        );

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(InvalidAttributeException.class, () -> userService.update(userRequest));
    }

    @Test
    @DisplayName("Should delete the user")
    void deleteCase1() {
        Long userId = 1L;
        String userName = "Lucas Gabriel";

        User user = new  User(
                userId,
                userName,
                "LucasDev123",
                Set.of(
                        RoleEnum.GESTOR,
                        RoleEnum.PROFESSOR,
                        RoleEnum.RESPONSAVEL
                )
        );

        UserResponseDTO userResponse = new  UserResponseDTO(
                userId,
                userName
        );

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toUserResponseDTO(user)).thenReturn(userResponse);

        userService.delete(userId);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw InvalidAttributeException when the user is not found")
    void deleteCase2() {
        Long userId = 1L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(InvalidAttributeException.class, () -> userService.delete(userId));
    }
}