package com.test.cria.service;

import com.test.cria.dto.user.UserPageResponseDTO;
import com.test.cria.dto.user.UserResponseDTO;
import com.test.cria.entity.User;
import com.test.cria.exception.user.UserNotFoundException;
import com.test.cria.mapper.UserMapper;
import com.test.cria.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDTO findById(Long id) {
        return this.userMapper.toUserResponseDTO(this.userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found")));
    }
    
    public UserPageResponseDTO findAllPaginated(int  page, int size) {
        Page<User> pageUser = this.userRepository.findAll(PageRequest.of(page, size));
        List<UserResponseDTO> userResponse = pageUser.get().map(this.userMapper::toUserResponseDTO).toList();

        return new UserPageResponseDTO(userResponse, pageUser.getTotalElements(), pageUser.getTotalPages());
    }
}
