package com.project.model.exeptions;

public class RgNotFoundException extends RuntimeException {
    public RgNotFoundException(String message) {
        super(message);
    }
}