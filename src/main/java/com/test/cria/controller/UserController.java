package com.test.cria.controller;

import com.test.cria.dto.user.UserPageResponseDTO;
import com.test.cria.dto.user.UserResponseDTO;
import com.test.cria.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public UserResponseDTO findById(@PathVariable @NotNull(message = "ID is required") @Positive(message = "ID must be greater than zero") Long id) {
        return userService.findById(id);
    }

    // ?page=<value>&size=<value>
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserPageResponseDTO list(@RequestParam(defaultValue = "0") @PositiveOrZero(message = "Page must be greater than or equal to zero") int page,
                                    @RequestParam(defaultValue = "10") @Positive(message = "Size must be greater than zero")
                                    @Max(value = 20, message = "Size must be less than or equal to 20") int size) {
        return userService.findAllPaginated(page, size);
    }
}
