package com.example.vetechspringboot.exception;

/**
 * Business rule validation failure.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
