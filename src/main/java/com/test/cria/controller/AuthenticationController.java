package com.test.cria.controller;

import com.test.cria.dto.auth.LoginRequestDTO;
import com.test.cria.dto.auth.AuthenticationResponseDTO;
import com.test.cria.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthenticationController {

    private AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Received request to login with email: {}", request.email());

        return ResponseEntity.ok(authenticationService.login(request));
    }
}
