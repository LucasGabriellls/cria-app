package com.test.cria.service;

import com.test.cria.dto.request.authRequest.LoginRequestDTO;
import com.test.cria.dto.request.authRequest.RegisterRequestDTO;
import com.test.cria.dto.response.authResponse.AuthenticationResponseDTO;
import com.test.cria.entity.User;
import com.test.cria.entity.enums.RoleEnum;
import com.test.cria.exception.userExceptions.UserNotFoundException;
import com.test.cria.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthenticationService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponseDTO register(RegisterRequestDTO request) {
        var user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Set.of(RoleEnum.PROFESSOR))
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponseDTO(jwtToken);
    }

    public AuthenticationResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        var jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponseDTO(jwtToken);
    }
}
