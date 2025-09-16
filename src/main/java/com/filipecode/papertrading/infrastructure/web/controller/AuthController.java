package com.filipecode.papertrading.infrastructure.web.controller;

import com.filipecode.papertrading.application.usecase.LoginUserUseCase;
import com.filipecode.papertrading.application.usecase.RegisterUserUseCase;
import com.filipecode.papertrading.infrastructure.web.dto.AuthResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.LoginUserRequestDTO;
import com.filipecode.papertrading.infrastructure.web.dto.RegisterUserRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUserUseCase loginUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
    }

    @PostMapping("register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterUserRequestDTO requestData) {
        AuthResponseDTO response = registerUserUseCase.register(requestData);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginUserRequestDTO requestData) {
        AuthResponseDTO response = loginUserUseCase.login(requestData);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
