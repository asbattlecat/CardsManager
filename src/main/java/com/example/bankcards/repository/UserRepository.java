package com.example.bankcards.repository;

import com.example.bankcards.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<UserEntity, UUID> {
  boolean existsByEmail(String email);
  Optional<UserEntity> findByEmail(String email);
}
