package com.example.bankcards.exception;

public class CardNumberIsBusyException extends RuntimeException {
  public CardNumberIsBusyException(String message) {
    super(message);
  }
}
