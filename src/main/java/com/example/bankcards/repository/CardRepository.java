package com.example.bankcards.repository;

import com.example.bankcards.entity.CardEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface CardRepository extends CrudRepository<CardEntity, UUID> {
  boolean existsByEncryptedNumber(String encryptedNumber);

  @Query(value = """
    SELECT ce.id
    FROM CardEntity AS ce
  """)
  List<UUID> getAllIdList();

  List<UUID> findIdsByOwnerId(UUID userId);
}
