package com.filipecode.papertrading.application.service;

import com.filipecode.papertrading.application.usecase.DeleteUserUseCase;
import com.filipecode.papertrading.application.usecase.LoginUserUseCase;
import com.filipecode.papertrading.application.usecase.RegisterUserUseCase;
import com.filipecode.papertrading.domain.exception.CpfAlreadyExistsException;
import com.filipecode.papertrading.domain.exception.InvalidCredentialsException;
import com.filipecode.papertrading.domain.exception.UserAlreadyExistsException;
import com.filipecode.papertrading.domain.exception.UserHasOpenPositionsException;
import com.filipecode.papertrading.domain.exception.UserNotFoundException;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import com.filipecode.papertrading.domain.model.user.User;
import com.filipecode.papertrading.domain.model.user.UserRole;
import com.filipecode.papertrading.domain.repository.UserRepositoryPort;
import com.filipecode.papertrading.domain.service.TokenProviderPort;
import com.filipecode.papertrading.infrastructure.web.dto.AuthResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.LoginUserRequestDTO;
import com.filipecode.papertrading.infrastructure.web.dto.RegisterUserRequestDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserService implements RegisterUserUseCase, LoginUserUseCase, DeleteUserUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final TokenProviderPort tokenProviderPort;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepositoryPort userRepositoryPort, TokenProviderPort tokenProviderPort, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.tokenProviderPort = tokenProviderPort;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public AuthResponseDTO register(RegisterUserRequestDTO requestData) {
        if (userRepositoryPort.findByEmail(requestData.email()).isPresent()) {
            throw new UserAlreadyExistsException("Tentativa de registro com e-mail já existente: " + requestData.email());
        }
        if (userRepositoryPort.findByCpf(requestData.cpf()).isPresent()) {
            throw new CpfAlreadyExistsException("Tentativa de registro com CPF já existente: " + requestData.cpf());
        }
        String encryptedPassword = passwordEncoder.encode(requestData.password());

        User newUser = new User();
        newUser.setName(requestData.name());
        newUser.setEmail(requestData.email());
        newUser.setPassword(encryptedPassword);
        newUser.setCpf(requestData.cpf());
        newUser.setRole(UserRole.valueOf("USER"));

        Portfolio portfolio = new Portfolio();
        portfolio.setBalance(new BigDecimal("100000.00"));
        portfolio.setUser(newUser);
        newUser.setPortfolio(portfolio);

        User savedUser = userRepositoryPort.save(newUser);

        String token = tokenProviderPort.generateToken(savedUser);

        return new AuthResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                token
        );
    }

    @Override
    public AuthResponseDTO login(LoginUserRequestDTO requestData) {
        try {
        var usernamePassword = new UsernamePasswordAuthenticationToken(requestData.email(), requestData.password());

        var auth = this.authenticationManager.authenticate(usernamePassword);

        var user = (User) auth.getPrincipal();

        String token = tokenProviderPort.generateToken(user);

        return new AuthResponseDTO(
                user.getId(),
                user.getName(),
                token
        );

        } catch(AuthenticationException exception) {
            throw new InvalidCredentialsException("E-mail ou senha inválidos");
        }
    }

    @Transactional
    @Override
    public void delete() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();

        User userToDelete = userRepositoryPort.findById(authenticatedUser.getId())
                .orElseThrow(() -> new UserNotFoundException("Usuário autenticado não encontrado no banco de dados. ID: " + authenticatedUser.getId()));

        if (!userToDelete.getPortfolio().getPositions().isEmpty()) {
            throw new UserHasOpenPositionsException("Não é possível deletar a conta. O usuário possui posições de ativos em carteira.");
        }

        userRepositoryPort.deleteById(userToDelete.getId()); 
    }
}
