package com.project.model.exeptions;

public class ClinicalRecordNotFoundException extends RuntimeException {
    public ClinicalRecordNotFoundException(String message) {
        super(message);
    }
}
