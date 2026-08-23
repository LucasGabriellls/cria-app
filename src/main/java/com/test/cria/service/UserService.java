package com.test.cria.service;

import com.test.cria.dto.request.userRequest.UserCreateRequestDTO;
import com.test.cria.dto.request.userRequest.UserUpdateRequestDTO;
import com.test.cria.dto.response.userResponse.UserPageResponseDTO;
import com.test.cria.dto.response.userResponse.UserResponseDTO;
import com.test.cria.entity.Role;
import com.test.cria.entity.User;
import com.test.cria.entity.enums.RoleEnum;
import com.test.cria.exception.userExceptions.UserAlreadyExistsException;
import com.test.cria.exception.userExceptions.UserNotFoundException;
import com.test.cria.mapper.UserMapper;
import com.test.cria.repository.RoleRepository;
import com.test.cria.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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

    @Transactional
    public UserResponseDTO create(UserCreateRequestDTO userRequest) {
        if (this.userRepository.existsByEmail(userRequest.email())) throw new UserAlreadyExistsException("User already exists");

        User userEntity = this.userMapper.toUserEntity(userRequest);

        userEntity.setPassword(this.passwordEncoder.encode(userRequest.password()));

        Set<Role> roles = userRequest.roles().stream()
                .map(userRole -> this.roleRepository.findByRole(userRole)
                        .orElseThrow(() -> new UserNotFoundException("Role not found: " + userRole)))
                .collect(Collectors.toSet());

        userEntity.setRoles(roles);

        return this.userMapper.toUserResponseDTO(this.userRepository.save(userEntity));
    }

    @Transactional
    public UserResponseDTO update(UserUpdateRequestDTO userRequest) {
        User userEntity = userRepository.findById(userRequest.id())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        if (!userEntity.getEmail().equals(userRequest.email()) && userRepository.existsByEmail(userRequest.email()))
            throw new UserAlreadyExistsException("Email already in use");

       userEntity.setFirstName(userRequest.firstName());
       userEntity.setLastName(userRequest.lastName());
       userEntity.setEmail(userRequest.email());

       userEntity.setPassword(this.passwordEncoder.encode(userRequest.password()));

       Set<Role> roles = userRequest.roles().stream()
               .map(userRole -> this.roleRepository.findByRole(userRole)
                       .orElseThrow(() -> new UserNotFoundException("Role not found: " + userRole)))
               .collect(Collectors.toSet());

       userEntity.setRoles(roles);

        return userMapper.toUserResponseDTO(userEntity);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) throw new UserNotFoundException("User not found!");

        userRepository.deleteById(id);
    }
}
