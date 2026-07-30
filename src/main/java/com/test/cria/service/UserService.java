package com.test.cria.service;

import com.test.cria.DTO.request.UserRequestDTO;
import com.test.cria.DTO.response.UserResponseDTO;
import com.test.cria.entity.User;
import com.test.cria.exception.UserNotFound;
import com.test.cria.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFound::new);

        return new UserResponseDTO (
                user.getId(),
                user.getCPF()
        );
    }
    
    public List<UserResponseDTO> findAll() {
        List<User> userTemp = userRepository.findAll();

        if  (userTemp.isEmpty()) throw new UserNotFound("Nenhum usuário encontrado!");

        return userTemp.stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getCPF()
                ))
                .toList();
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO user) {
        User userTemp = new User(
                user.id(),
                user.CPF(),
                user.password()
        );

        User tempUser = userRepository.save(userTemp);

        return new UserResponseDTO(
                userTemp.getId(),
                userTemp.getCPF()
        );
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
