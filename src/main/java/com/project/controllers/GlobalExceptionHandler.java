package com.project.controllers;

import com.project.model.exeptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Classe de manipulação global de exceções.
 * Cada método de tratamento de exceção que retorna uma resposta HTTP apropriada.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @return Resposta HTTP com status 404 (Not Found).
     */
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<String> emailNotFoundException(EmailNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * @return Retorna uma resposta com o código 400 (Bad Request)
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<String> emailAlreadyExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * @return Resposta HTTP com status 404 (Not Found).
     */
    @ExceptionHandler(CpfNotFoundException.class)
    public ResponseEntity<String> cpfNotFoundException(CpfNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * @return Resposta HTTP com status 404 (Not Found).
     */
    @ExceptionHandler(RgNotFoundException.class)
        public ResponseEntity<String> rgNotFoundException(RgNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * @return Resposta HTTP com status 403 (Forbidden).
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> invalidCredentialsException(InvalidCredentialsException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    /**
     * @return Resposta HTTP com status 404 (Not Found).
     */
    @ExceptionHandler(AnimalNotFoundException.class)
    public ResponseEntity<String> notFoundException(AnimalNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(InvalidRecoveryCodeException.class)
    public ResponseEntity<String> invalidCode(InvalidRecoveryCodeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidAnimalAccessException.class)
    public ResponseEntity<String> invalidAccess(InvalidAnimalAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

}
