package com.example.bankcards.repository;

import com.example.bankcards.entity.CardEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends CrudRepository<CardEntity, UUID> {
  boolean existsByEncryptedNumber(String encryptedNumber);

  @Query(value = """
    SELECT ce.id
    FROM CardEntity AS ce
  """)
  List<UUID> getAllIdList();

  List<UUID> findIdsByOwnerId(UUID userId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(value = """
    SELECT ce
    FROM CardEntity AS ce
    WHERE ce.id = :id
  """)
  Optional<CardEntity> findByIdWithPessimisticLock(@Param("id") UUID cardId);
}
