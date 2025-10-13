package com.filipecode.papertrading.infrastructure.web.controller;

import com.filipecode.papertrading.application.usecase.LoginUserUseCase;
import com.filipecode.papertrading.application.usecase.RegisterUserUseCase;
import com.filipecode.papertrading.infrastructure.web.dto.AuthResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.ErrorResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.LoginUserRequestDTO;
import com.filipecode.papertrading.infrastructure.web.dto.RegisterUserRequestDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth Management", description = "Controller para autenticação de usuário")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUserUseCase loginUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
    }

    @PostMapping("register")
    @Operation(
        summary = "Registro de novo usuário",
        description = "Cria um novo usuário no sistema e retorna um token JWT de autenticação"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso",
                        content = @Content(mediaType = "application/json",                         
                        schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou malformados",
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Não possui as permissões necessárias para acessar o recurso solicitado",
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Usuário já cadastrado (e-mail duplicado)",
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterUserRequestDTO requestData) {
        AuthResponseDTO response = registerUserUseCase.register(requestData);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("login")
    @Operation(summary = "Login de usuário", description = "Realiza a autenticação de um usuário no sistema"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso",
                        content = @Content(mediaType = "application/json", 
                        schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas (e-mail ou senha incorretos)",
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Não possui as permissões necessárias para acessar o recurso solicitado",
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginUserRequestDTO requestData) {                     
        AuthResponseDTO response = loginUserUseCase.login(requestData);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
