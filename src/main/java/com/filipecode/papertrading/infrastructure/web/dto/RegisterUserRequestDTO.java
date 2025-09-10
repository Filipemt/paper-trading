package com.filipecode.papertrading.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequestDTO(@NotBlank String name,
                                     @NotBlank @Email String email,
                                     @NotBlank @Size(min = 8) String password,
                                     @NotBlank String cpf) {
}
