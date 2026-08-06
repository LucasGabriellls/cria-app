package com.test.cria.controller;

import com.test.cria.dto.request.userRequest.UserCreateRequestDTO;
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
    public UserResponseDTO findById(@PathVariable @NotNull(message = "ID is required") @Positive(message = "ID must be greater than zero") Long id) { // tratar o notnull
        return userService.findById(id);
    }

    // ?page=<value>&size=<value>
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserPageResponseDTO list(@RequestParam(defaultValue = "0") @PositiveOrZero(message = "Page must be greater than or equal to zero") int page,
                                    @RequestParam(defaultValue = "10") @Positive(message = "Size must be greater than zero")
                                    @Max(value = 20, message = "Size must be less than or equal to 20") int size) {
        return userService.list(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(@Valid @RequestBody UserCreateRequestDTO user) {
        return userService.create(user);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO update(@Valid @RequestBody UserUpdateRequestDTO user) {
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable  @NotNull(message = "ID is required") @Positive(message = "ID must be greater than zero") Long id) {
        userService.delete(id);
    }
}
