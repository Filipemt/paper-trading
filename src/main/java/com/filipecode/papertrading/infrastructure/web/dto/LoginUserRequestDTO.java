package com.filipecode.papertrading.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginUserRequestDTO(
        @NotBlank(message = "O E-mail não pode estar em branco.")
        @Email(message = "E-mail fornecido não é válido.")
        String email,

        // Futura atualização: Adicionar campo de login com o CPF do usuário

        @NotBlank(message = "A senha não pode estar em branco.")
        @Size(min = 8, message = "Senha deve possuir no mínimo 8 caracteres.")
        String password) {
}
