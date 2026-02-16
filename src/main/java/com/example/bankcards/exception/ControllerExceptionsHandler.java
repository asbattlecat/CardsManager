package com.example.bankcards.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionsHandler {
  @ExceptionHandler(AlreadyExistsException.class)
  public ResponseEntity<String> handleUserAlreadyExists(AlreadyExistsException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<String> handleInvalidLogin(InvalidCredentialsException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleUserNotFound(NotFoundException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }
}
