package com.test.cria.controller;

import com.test.cria.DTO.request.UserRequestDTO;
import com.test.cria.DTO.response.UserResponseDTO;
import com.test.cria.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        if (id != null) throw new IllegalArgumentException("Id de usuário é necessário!");
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @PostMapping("/user")
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserRequestDTO user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(user));
    }

    @PutMapping("/user")
    public ResponseEntity<UserResponseDTO> update(@RequestBody UserRequestDTO user) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(user));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (id != null) throw new IllegalArgumentException("Id de usuário é necessário!");
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
