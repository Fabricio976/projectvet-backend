package com.project.model.exeptions;

public class InvalidAnimalAccessException extends RuntimeException {
    public InvalidAnimalAccessException(String message) {
        super(message);
    }
}
