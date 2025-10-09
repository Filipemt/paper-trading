package com.filipecode.papertrading.domain.exception;

public class UnsupportedOrderTypeException extends RuntimeException{
    public UnsupportedOrderTypeException(String message) {
        super(message);
    }
    
}
