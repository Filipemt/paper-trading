package com.filipecode.papertrading.domain.exception;

public class UserHasOpenPositionsException extends RuntimeException {
    public UserHasOpenPositionsException(String message) {
        super(message);
    }
}
