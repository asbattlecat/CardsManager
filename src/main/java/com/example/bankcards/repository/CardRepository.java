package com.example.bankcards.repository;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CardRepository extends CrudRepository<CardEntity, UUID> {
  boolean existsByEncryptedNumber(String encryptedNumber);

  @Query(value = """
    SELECT ce.id
    FROM CardEntity AS ce
  """)
  List<UUID> getAllIdList();

  List<UUID> findIdsByOwnerId(UUID userId);

  Page<CardEntity> findByOwnerId(UUID ownerId, Pageable pageable);
  Page<CardEntity> findByOwnerIdAndStatus(UUID ownerId, CardStatus status, Pageable pageable);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(value = """
    SELECT ce
    FROM CardEntity AS ce
    WHERE ce.id = :id
  """)
  Optional<CardEntity> findByIdWithPessimisticLock(@Param("id") UUID cardId);
}
