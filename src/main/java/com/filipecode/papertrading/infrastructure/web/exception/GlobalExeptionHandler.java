package com.filipecode.papertrading.infrastructure.web.exception;

import com.filipecode.papertrading.domain.exception.CpfAlreadyExistsException;
import com.filipecode.papertrading.domain.exception.UserAlreadyExistsException;
import com.filipecode.papertrading.infrastructure.web.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExeptionHandler {

    private static final String GENERIC_CONFLICT_MESSAGE = "E-mail ou CPF já está cadastrado. Por favor, verifique os dados.";

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyExists(UserAlreadyExistsException exception) {
        log.warn("Conflito no registro de usuário. Causa: {}", exception.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                GENERIC_CONFLICT_MESSAGE,
                java.time.LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CpfAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleCpfAlreadyExists(CpfAlreadyExistsException exception) {
            log.warn("Conflito no registro de usuário. Causa: {}", exception.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                GENERIC_CONFLICT_MESSAGE,
                java.time.LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException exception) {
        log.warn("Falha na autenticação. Causa: {}", exception.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "E-mail ou senha inválidos.",
                java.time.LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handler específico para erros de validação da anotação @Valid.
     * Retorna um status 400 Bad Request com um corpo JSON detalhando
     * todos os campos que falharam na validação.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Erro de validação na requisição: {}", errors);
        return errors;
    }


}
