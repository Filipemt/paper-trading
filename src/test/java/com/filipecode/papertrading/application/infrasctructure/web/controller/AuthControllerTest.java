package com.filipecode.papertrading.application.infrasctructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.filipecode.papertrading.application.usecase.RegisterUserUseCase;
import com.filipecode.papertrading.domain.exception.UserAlreadyExistsException;
import com.filipecode.papertrading.infrastructure.config.security.DevSecurityConfig;
import com.filipecode.papertrading.infrastructure.web.controller.AuthController;
import com.filipecode.papertrading.infrastructure.web.dto.AuthResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.RegisterUserRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(DevSecurityConfig.class)
@ActiveProfiles("dev")           // Garante que o perfil 'dev' seja ativado para esta classe
class AuthControllerTest {

    // Ferramenta do Spring para simular requisições HTTP.
    @Autowired
    private MockMvc mockMvc;

    // Utilitário para converter objetos Java para JSON e vice-versa.
    @Autowired
    private ObjectMapper objectMapper;

    // Mock do Caso de Uso. Como o Service não é carregado,
    // nós simulamos seu comportamento.
    @MockBean
    private RegisterUserUseCase registerUserUseCase;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;
    // Futuramente, adicionaremos mocks para outros UseCases aqui.

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso e retornar status 201 Created com o DTO de autenticação")
    void register_comDadosValidos_deveRetornarStatus201EAuthDTO() throws Exception {
        // --- ARRANGE (Arrumar o cenário) ---

        var requestDTO = new RegisterUserRequestDTO("Filipe", "filipe@email.com", "password123", "123.456.789-00");
        var expectedResponseDTO = new AuthResponseDTO(1L, "Filipe", "fake-jwt-token-123");

        // Configura o mock para o cenário de SUCESSO.
        when(registerUserUseCase.execute(any(RegisterUserRequestDTO.class)))
                .thenReturn(expectedResponseDTO);

        // --- ACT & ASSERT (Agir e Afirmar) ---

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated()) // Espera o status 201 Created
                .andExpect(jsonPath("$.userId").value(expectedResponseDTO.userId()))
                .andExpect(jsonPath("$.name").value(expectedResponseDTO.name()))
                .andExpect(jsonPath("$.token").value(expectedResponseDTO.token()));
    }

    @Test
    @DisplayName("Deve retornar status 409 Conflict quando registro falhar por usuário já existente")
    void register_comEmailDuplicado_deveRetornarStatus409() throws Exception {
        // --- ARRANGE (Arrumar o cenário) ---

        var requestDTO = new RegisterUserRequestDTO("John Doe", "john.doe@example.com", "password123", "12345678901");

        // Configura o mock para o cenário de FALHA.
        when(registerUserUseCase.execute(any(RegisterUserRequestDTO.class)))
                .thenThrow(new UserAlreadyExistsException("E-mail já cadastrado."));

        // --- ACT & ASSERT (Agir e Afirmar) ---

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict()) // Espera o status 409 Conflict
                .andExpect(jsonPath("$.message").value("E-mail ou CPF já está cadastrado. Por favor, verifique os dados."));
    }
}