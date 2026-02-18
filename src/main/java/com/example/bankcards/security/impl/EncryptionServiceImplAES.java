package com.example.bankcards.security.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.example.bankcards.security.interfaces.EncryptionService;
import org.springframework.stereotype.Service;

@Service
public class EncryptionServiceImplAES implements EncryptionService {
  private static final String ALGORITHM = "AES";
  private final SecretKeySpec key;

  public EncryptionServiceImplAES() {
    String secret = "abc_abc_abc_abc_abc_abc_abc_abc_";
    key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
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
