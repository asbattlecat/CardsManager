package com.example.bankcards.exception;

import javax.management.relation.InvalidRelationIdException;
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
  public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleNotFound(NotFoundException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalState(IllegalStateException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(InvalidIdException.class)
  public ResponseEntity<String> handleInvalidId(InvalidIdException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(InsufficientFundsException.class)
  public ResponseEntity<String> handleInsufficientFound(InsufficientFundsException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }
}
