package com.filipecode.papertrading.domain.exception;

public class InsufficientPositionException extends RuntimeException {
    public InsufficientPositionException(String message) {
        super(message);
    }
}
