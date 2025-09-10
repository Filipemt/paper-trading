package com.filipecode.papertrading.application.service;

import com.filipecode.papertrading.application.usecase.RegisterUserUseCase;
import com.filipecode.papertrading.domain.exception.CpfAlreadyExistsException;
import com.filipecode.papertrading.domain.exception.UserAlreadyExistsException;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import com.filipecode.papertrading.domain.model.user.User;
import com.filipecode.papertrading.domain.repository.UserRepositoryPort;
import com.filipecode.papertrading.domain.service.TokenProviderPort;
import com.filipecode.papertrading.infrastructure.web.dto.AuthResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.RegisterUserRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService implements RegisterUserUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final TokenProviderPort tokenProviderPort;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepositoryPort userRepositoryPort, TokenProviderPort tokenProviderPort, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.tokenProviderPort = tokenProviderPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponseDTO execute(RegisterUserRequestDTO requestData) {
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
}
