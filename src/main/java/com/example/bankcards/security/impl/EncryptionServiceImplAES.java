package com.example.bankcards.security.impl;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.example.bankcards.security.interfaces.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionServiceImplAES implements EncryptionService {
  private static final String ALGORITHM = "AES";
  private final SecretKeySpec key;

  public EncryptionServiceImplAES(@Value("${ENCRYPTION_SECRET_FILE:}") String secretFilePath) throws Exception {
    String secret;

    if (secretFilePath != null && !secretFilePath.isEmpty() && Files.exists(Paths.get(secretFilePath))) {
      // Читаем секрет из Docker secret
      secret = Files.readString(Path.of(secretFilePath)).trim();
    } else {
      // Fallback для разработки
      secret = System.getenv().getOrDefault("ENCRYPTION_SECRET",
              "abc_abc_abc_abc_abc_abc_abc_abc_"); // только для разработки без docker secret!!
    }

    // Для AES ключ должен быть 16, 24 или 32 байта
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
      throw new IllegalArgumentException("AES key must be 16, 24, or 32 bytes long");
    }

    this.key = new SecretKeySpec(keyBytes, ALGORITHM);
  }

  @Override
  public String encrypt(String plainText) {
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] encryptedPlainText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(encryptedPlainText);
    } catch (Exception e) {
      throw new RuntimeException("Encryption error: " + e.getMessage());
    }
  }

  @Override
  public String decrypt(String cipherText) {
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] decoded = Base64.getDecoder().decode(cipherText);
      return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new RuntimeException("Decryption error: " + e.getMessage());
    }
  }
}
