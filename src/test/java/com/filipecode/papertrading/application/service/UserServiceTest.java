package com.filipecode.papertrading.application.service;

import com.filipecode.papertrading.domain.exception.CpfAlreadyExistsException;
import com.filipecode.papertrading.domain.exception.UserAlreadyExistsException;
import com.filipecode.papertrading.domain.model.user.User;
import com.filipecode.papertrading.application.port.out.UserRepositoryPort;
import com.filipecode.papertrading.application.port.out.TokenProviderPort;
import com.filipecode.papertrading.infrastructure.web.dto.AuthResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.RegisterUserRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private TokenProviderPort tokenProviderPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso e retornar um DTO de autenticação")
    void execute_comDadosValidos_deveRegistrarUsuarioEretornarAuthDTO() {
        // Arrange
        var request = new RegisterUserRequestDTO("John Doe", "john.doe@example.com", "password123", "12345678901");
        when(userRepositoryPort.findByEmail(request.email())).thenReturn(Optional.empty());
        when(userRepositoryPort.findByCpf(request.cpf())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName(request.name());
        savedUser.setEmail(request.email());

        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);
        when(tokenProviderPort.generateToken(savedUser)).thenReturn("jwt-token");

        // Act
        AuthResponseDTO response = userService.register(request);

        // Assert
        verify(userRepositoryPort).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertEquals(request.name(), capturedUser.getName());
        assertEquals(request.email(), capturedUser.getEmail());
        assertEquals("encodedPassword", capturedUser.getPassword());
        assertEquals(request.cpf(), capturedUser.getCpf());
        assertNotNull(capturedUser.getPortfolio());
        assertEquals(0, new BigDecimal("100000.00").compareTo(capturedUser.getPortfolio().getBalance()));
        assertEquals(capturedUser, capturedUser.getPortfolio().getUser());

        assertNotNull(response);
        assertEquals(savedUser.getId(), response.userId());
        assertEquals(savedUser.getName(), response.name());
        assertEquals("jwt-token", response.token());
    }

    @Test
    @DisplayName("Deve lançar UserAlreadyExistsException quando o email já está em uso")
    void execute_comEmailExistente_deveLancarUserAlreadyExistsException() {
        // Arrange
        var request = new RegisterUserRequestDTO("Jane Doe", "jane.doe@example.com", "password123", "09876543210");
        when(userRepositoryPort.findByEmail(request.email())).thenReturn(Optional.of(new User()));

        // Act & Assert
        var exception = assertThrows(UserAlreadyExistsException.class, () -> userService.register(request));
        assertEquals("Attempt to register with an already existing email: " + request.email(), exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar CpfAlreadyExistsException quando o CPF já está em uso")
    void execute_comCpfExistente_deveLancarCpfAlreadyExistsException() {
        // Arrange
        var request = new RegisterUserRequestDTO("Jane Doe", "jane.doe@example.com", "password123", "09876543210");
        when(userRepositoryPort.findByEmail(request.email())).thenReturn(Optional.empty());
        when(userRepositoryPort.findByCpf(request.cpf())).thenReturn(Optional.of(new User()));

        // Act & Assert
        var exception = assertThrows(CpfAlreadyExistsException.class, () -> userService.register(request));
        assertEquals("Attempt to register with an already existing CPF: " + request.cpf(), exception.getMessage());
    }
}
