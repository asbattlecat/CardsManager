package com.example.bankcards.security.interfaces;

public interface EncryptionService {
  String encrypt(String text);
  String decrypt(String text);
}
