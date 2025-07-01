package com.project.model.exeptions;

public class InvalidRecoveryCodeException extends RuntimeException {
  public InvalidRecoveryCodeException(String message) {
    super(message);
  }
}
