package com.mrthinkj.core.exception;

public class DoesNotExistException extends RuntimeException{
    public DoesNotExistException(String message) {
        super(message);
    }
}
