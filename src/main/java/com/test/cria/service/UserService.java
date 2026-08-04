package com.test.cria.service;

import com.test.cria.DTO.request.UserRequestDTO;
import com.test.cria.DTO.response.UserResponseDTO;
import com.test.cria.entity.User;
import com.test.cria.exception.userExceptions.InvalidAttributeException;
import com.test.cria.mapper.UserMapper;
import com.test.cria.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,  UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDTO findById(Long id) {
        if (id <= 0) throw new InvalidAttributeException("O id do usuario deve ser maior que 0!");
        return userMapper.toUserResponseDTO(userRepository.findById(id).orElseThrow(() -> new InvalidAttributeException("Usuário não encontrado!")));
    }
    
    public Page<UserResponseDTO> list(int  page, int size) {

        Page<User> pageUser = userRepository.findAll(PageRequest.of(page, size));

        return pageUser.map(userMapper::toUserResponseDTO);
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO user) {

        User userTemp = userMapper.toUserEntity(user);

        return  userMapper.toUserResponseDTO(userRepository.save(userTemp));
    }

    @Transactional
    public UserResponseDTO update(UserRequestDTO user) {
        if (user.id() == null || user.id() <= 0) {
            throw new InvalidAttributeException("O id do usuário deve ser maior que 0!");
        }

        User userEntity = userRepository.findById(user.id())
                .orElseThrow(() -> new InvalidAttributeException("Usuário não encontrado!"));

        userEntity.setUserName(user.userName());
        userEntity.setPassword(user.password());
        userEntity.setRole(user.role());

        User updatedUser = userRepository.save(userEntity);
        return userMapper.toUserResponseDTO(updatedUser);
    }

    @Transactional
    public void delete(Long id) {

        UserResponseDTO userResponseDTO = findById(id);

        if (userResponseDTO == null) throw new InvalidAttributeException("Usuário não encontrado!");

        userRepository.deleteById(id);
    }
}
