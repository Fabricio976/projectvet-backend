package com.project.model.exeptions;

public class TemplateProcessingException extends RuntimeException {
    public TemplateProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
