package com.test.cria.service;

import com.test.cria.DTO.request.UserRequestDTO;
import com.test.cria.DTO.response.UserResponseDTO;
import com.test.cria.entity.User;
import com.test.cria.exception.userExceptions.InvalidAttributeException;
import com.test.cria.mapper.UserMapper;
import com.test.cria.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    public UserService(UserRepository userRepository,  UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDTO findById(Long id) {
        if (id <= 0) throw new InvalidAttributeException("O id do usuario deve ser maior que 1!");
        return userMapper.toUserResponseDTO(userRepository.findById(id).orElseThrow(() -> new InvalidAttributeException("Usuário não encontrado!")));
    }
    
    public List<UserResponseDTO> findAll() {
        List<UserResponseDTO> userTemp = userMapper.toUserResponseDTO(userRepository.findAll());

        if  (userTemp.isEmpty()) throw new InvalidAttributeException("Nenhum usuário encontrado!");

        return userTemp;
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO user) {

        User userTemp = userMapper.toUserEntity(user);

        return  userMapper.toUserResponseDTO(userRepository.save(userTemp));
    }

    @Transactional
    public UserResponseDTO update(UserRequestDTO user) {
        UserResponseDTO userRequestDTO = findById(user.id());

        return create(userMapper.toUserRequestDTO(userRequestDTO));
    }

    @Transactional
    public void delete(Long id) {

        UserResponseDTO userResponseDTO = findById(id);

        userRepository.deleteById(id);
    }
}
