package com.test.cria.service;

import com.test.cria.dto.request.userRequest.UserCreateRequestDTO;
import com.test.cria.dto.request.userRequest.UserUpdateRequestDTO;
import com.test.cria.dto.response.userResponse.UserPageResponseDTO;
import com.test.cria.dto.response.userResponse.UserResponseDTO;
import com.test.cria.entity.User;
import com.test.cria.exception.userExceptions.UserAlreadyExistsException;
import com.test.cria.exception.userExceptions.UserNotFoundException;
import com.test.cria.mapper.UserMapper;
import com.test.cria.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,  UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDTO findById(Long id) {
        return userMapper.toUserResponseDTO(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found")));
    }
    
    public UserPageResponseDTO list(int  page, int size) {
        Page<User> pageUser = userRepository.findAll(PageRequest.of(page, size));
        List<UserResponseDTO> userResponse = pageUser.get().map(userMapper::toUserResponseDTO).toList();

        return new UserPageResponseDTO(userResponse, pageUser.getTotalElements(), pageUser.getTotalPages());
    }

    @Transactional
    public UserResponseDTO create(UserCreateRequestDTO user) {
        if (userRepository.existsByUsername(user.username())) throw new UserAlreadyExistsException("User already exists");

        User userTemp = userMapper.toUserEntity(user);

        return  userMapper.toUserResponseDTO(userRepository.save(userTemp));
    }

    @Transactional
    public UserResponseDTO update(UserUpdateRequestDTO user) {
        User userEntity = userRepository.findById(user.id())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        userEntity.setUsername(user.username());
        userEntity.setPassword(user.password());
        userEntity.setRole(user.role());

        return userMapper.toUserResponseDTO(userEntity);
    }

    @Transactional
    public void delete(Long id) {
        UserResponseDTO userResponseDTO = findById(id);

        userRepository.deleteById(id);
    }
}
