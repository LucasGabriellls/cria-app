package com.test.cria.service;

import com.test.cria.DTO.request.UserRequestDTO;
import com.test.cria.DTO.response.UserResponseDTO;
import com.test.cria.entity.User;
import com.test.cria.exception.UserNotFound;
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
        return userMapper.toUserResponseDTO(userRepository.findById(id).orElseThrow(UserNotFound::new));
    }
    
    public List<UserResponseDTO> findAll() {
        List<UserResponseDTO> userTemp = userMapper.toUserResponseDTO(userRepository.findAll());

        if  (userTemp.isEmpty()) throw new UserNotFound("Nenhum usuário encontrado!");

        return userTemp;
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO user) {

        User userTemp = userMapper.toUserEntity(user);

        return  userMapper.toUserResponseDTO(userRepository.save(userTemp));
    }

    @Transactional
    public UserResponseDTO update(UserRequestDTO user) {

        UserResponseDTO userRequestDTOTemp = findById(user.id());

        if (userRequestDTOTemp != null) {
            return create(user);
        } else {
            throw new UserNotFound();
        }
    }

    @Transactional
    public void delete(Long id) {

        UserResponseDTO user = findById(id);

        if (user != null) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFound();
        }
    }
}
