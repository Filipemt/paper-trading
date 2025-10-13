package com.filipecode.papertrading.infrastructure.web.controller;

import com.filipecode.papertrading.application.usecase.DeleteUserUseCase;
import com.filipecode.papertrading.application.usecase.UpdateUserUseCase;
import com.filipecode.papertrading.infrastructure.web.dto.AuthResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.ErrorResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.UpdateUserRequestDTO;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Controller para gerenciamento de usuários")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final DeleteUserUseCase deleteUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    public UserController(DeleteUserUseCase deleteUserUseCase, UpdateUserUseCase updateUserUseCase) {
        this.deleteUserUseCase = deleteUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
    }

    @DeleteMapping("/me")
    @Operation(summary = "Deleta o usuário autenticado", description = "Remove permanentemente o usuário e todos os seus dados associados (portfólio, ordens, etc.). Esta ação é irreversível.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não Autenticado. É necessário enviar um token JWT válido no cabeçalho 'Authorization'.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Não possui as permissões necessárias para acessar o recurso solicitado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflito. O usuário possui posições abertas e não pode ser deletado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> deleteUser() {
        deleteUserUseCase.delete();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me")
    @Operation(summary = "Atualiza dados do usuário autenticado", description = "Permite a atualização de nome, e-mail e/ou senha. Apenas os campos fornecidos serão atualizados.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados para atualização. Todos os campos são opcionais.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UpdateUserRequestDTO.class),
                    examples = {
                            @ExampleObject(name = "Atualizar apenas o nome", value = "{\"name\": \"Novo Nome Completo\"}"),
                            @ExampleObject(name = "Atualizar e-mail e senha", value = "{\"email\": \"novo.email@example.com\", \"password\": \"novaSenhaSegura123\"}")
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do usuário atualizados com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos. Verifique os campos e tente novamente.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflito. O e-mail fornecido já está em uso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<AuthResponseDTO> updateUser(@RequestBody @Valid UpdateUserRequestDTO requestData) {
        AuthResponseDTO response = updateUserUseCase.update(requestData);
        return ResponseEntity.ok(response);
    }
}