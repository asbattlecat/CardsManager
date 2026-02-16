package com.example.bankcards.security;

public interface EncryptionService {
  String encrypt(String text);
  String decrypt(String text);
}
