package com.test.cria.controller;

import com.test.cria.dto.request.userRequest.UserRequestDTO;
import com.test.cria.dto.request.userRequest.UserUpdateRequestDTO;
import com.test.cria.dto.response.userResponse.UserPageResponseDTO;
import com.test.cria.dto.response.userResponse.UserResponseDTO;
import com.test.cria.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuário")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //@Operation
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO findById(@PathVariable @NotNull(message = "ID is required") @Positive(message = "ID must be greater than 0") Long id) {
        return userService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserPageResponseDTO list(@RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                    @RequestParam(defaultValue = "10") @Positive @Max(20) int size) {
        return userService.list(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(@Valid @RequestBody UserRequestDTO user) {
        return userService.create(user);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO update(@Valid @RequestBody UserUpdateRequestDTO user) {
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable  @NotNull(message = "ID is required") @Positive(message = "ID must be greater than 0") Long id) {
        userService.delete(id);
    }
}
