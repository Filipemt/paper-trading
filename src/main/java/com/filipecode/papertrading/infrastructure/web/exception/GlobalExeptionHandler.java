package com.filipecode.papertrading.infrastructure.web.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.filipecode.papertrading.domain.exception.*;
import com.filipecode.papertrading.infrastructure.web.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExeptionHandler {

    private static final String GENERIC_CONFLICT_MESSAGE = "E-mail ou CPF já está cadastrado. Por favor, verifique os dados.";

    @ExceptionHandler({HttpMessageNotReadableException.class, UnsupportedOrderTypeException.class})
     public ResponseEntity<ErrorResponseDTO> handleInvalidOrderType(Exception exception) {
        String message;

        if (exception instanceof HttpMessageNotReadableException httpEx &&
                httpEx.getCause() instanceof InvalidFormatException invalidFormat &&
                invalidFormat.getTargetType().isEnum()) {

            String invalidValue = String.valueOf(invalidFormat.getValue());
            message = "Tipo de ordem não suportado: " + invalidValue;
            log.warn("Enum inválido recebido: {}", invalidValue);

        } else if (exception instanceof UnsupportedOrderTypeException unsupportedEx) {
            message = unsupportedEx.getMessage();
            log.warn("Tipo de ordem não suportado. Causa: {}", unsupportedEx.getMessage());

        } else {
            message = "Corpo da requisição inválido.";
            log.warn("Erro ao processar corpo da requisição: {}", exception.getMessage());
        }

    ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            message,
            LocalDateTime.now()
    );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("Tentativa de login falhou: {}", ex.getMessage());

    return new ErrorResponseDTO(
            HttpStatus.UNAUTHORIZED.value(),
            ex.getMessage(),
            LocalDateTime.now()
    );
}


    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientFunds(InsufficientFundsException exception) {
        log.warn("Saldo insuficiente. Causa: {}", exception.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                java.time.LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PortfolioNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handlePortfolioNotFound(PortfolioNotFoundException exception) {
        log.warn("Portfólio não encontrado. Causa: {}", exception.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                java.time.LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AssetNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleAssetNotFound(AssetNotFoundException exception) {
        log.warn("Ativo não encontrado. Causa: {}", exception.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                java.time.LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException exception) {
        log.warn("Usuário não encontrado. Causa: {}", exception.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Usuário não encontrado.",
                java.time.LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserHasOpenPositionsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserHasOpenPositions(UserHasOpenPositionsException exception) {
        log.warn("Falha ao deletar usuário. Causa: {}", exception.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
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


    @ExceptionHandler(PositionNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handlePositionNotFoundException(PositionNotFoundException exception) {
        log.warn("Falha na busca de uma posição. Causa: {}", exception.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Posição não encontrada.",
                java.time.LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientPositionException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientPositionException(InsufficientPositionException exception) {
        log.warn("Falha na venda de um ativo. Causa: {}", exception.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Quantidade de posições insuficiente",
                java.time.LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    /**
     * Handler específico para erros de validação da anotação @Valid.
     * Retorna um status 400 Bad Request com um corpo JSON detalhando
     * todos os campos que falharam na validação.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
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
