package com.example.integradora5d.error.errorTypes;

public class CustomNotFoundException extends RuntimeException {
    public CustomNotFoundException(String message){
        super(message);
    }
}
