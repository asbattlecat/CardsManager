package com.example.bankcards.service.interfaces;

import com.example.bankcards.entity.UserEntity;
import java.util.UUID;

public interface UserRepositoryService {
  UserEntity getById(UUID userId);
  UserEntity getByEmail(String email);
  void validateExistsById(UUID userId);
  UserEntity save(UserEntity user);
  void validateNotExistsByEmail(String email);
}
