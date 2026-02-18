package com.example.bankcards.repository;

import com.example.bankcards.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, UUID> {
  boolean existsByEmail(String email);
  Optional<UserEntity> findByEmail(String email);
  Optional<UserEntity> findById(String id);
}
