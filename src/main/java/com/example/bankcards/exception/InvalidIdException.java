package com.example.bankcards.exception;

public class InvalidIdException extends RuntimeException {
  public InvalidIdException(String message) {
    super(message);
  }
}
