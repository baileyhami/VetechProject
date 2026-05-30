package com.example.projectbackend.service;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}


