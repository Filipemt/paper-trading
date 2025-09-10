package com.filipecode.papertrading.infrastructure.web.exception;

import com.filipecode.papertrading.domain.exception.CpfAlreadyExistsException;
import com.filipecode.papertrading.domain.exception.UserAlreadyExistsException;
import com.filipecode.papertrading.infrastructure.web.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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


}
