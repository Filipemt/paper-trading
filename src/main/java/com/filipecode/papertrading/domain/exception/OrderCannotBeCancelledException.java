package com.filipecode.papertrading.domain.exception;

public class OrderCannotBeCancelledException extends RuntimeException {
    public OrderCannotBeCancelledException(String message) {
        super(message);
    }
}