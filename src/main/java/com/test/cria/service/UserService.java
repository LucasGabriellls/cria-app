package com.test.cria.service;

import com.test.cria.dto.user.UserPageResponseDTO;
import com.test.cria.dto.user.UserResponseDTO;
import com.test.cria.entity.User;
import com.test.cria.exception.user.UserNotFoundException;
import com.test.cria.mapper.UserMapper;
import com.test.cria.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDTO findById(Long id) {
        log.debug("Searching for user with ID: {} in database", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with ID: {} not found in database", id);
                    return new UserNotFoundException("User not found");
                });

        log.debug("User with ID: {} found in database", id);

        return userMapper.toUserResponseDTO(user);
    }
    
    public UserPageResponseDTO findAllPaginated(int  page, int size) {
        log.debug("Fetching paginated users from database [page={}, size={}]", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<User> pageUser = userRepository.findAll(pageable);

        List<UserResponseDTO> userResponse = pageUser.getContent().stream()
                .map(this.userMapper::toUserResponseDTO)
                .toList();

        log.debug("Successfully fetched {} users from database [totalElements={}, totalPages={}]",
                userResponse.size(), pageUser.getTotalElements(), pageUser.getTotalPages());

        return new UserPageResponseDTO(userResponse,
                pageUser.getTotalElements(),
                pageUser.getTotalPages());
    }
}
