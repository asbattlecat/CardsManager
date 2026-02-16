package com.example.bankcards.exception;

public class UserAlreadyExists extends RuntimeException {
  public UserAlreadyExists(String message) {
    super(message);
  }
}
