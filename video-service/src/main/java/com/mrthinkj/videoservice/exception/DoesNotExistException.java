package com.mrthinkj.videoservice.exception;

public class DoesNotExistException extends RuntimeException{
    public DoesNotExistException(String message) {
        super(message);
    }
}
