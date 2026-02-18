package com.example.bankcards.service.interfaces;

import com.example.bankcards.entity.UserEntity;
import java.util.UUID;

public interface UserRepositoryService {
  UserEntity get(UUID userId);
  UserEntity get(String userId);
  UserEntity getByEmail(String email);
  void existsById(UUID userId);
  UserEntity save(UserEntity user);
  void validateNotExistsByEmail(String email);
}
