package com.example.bankcards.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionsHandler {
  @ExceptionHandler(UserAlreadyExists.class)
  public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExists e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(InvalidLoginException.class)
  public ResponseEntity<String> handleInvalidLogin(InvalidLoginException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(InvalidPasswordException.class)
  public ResponseEntity<String> handleInvalidPassword(InvalidPasswordException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }
}
