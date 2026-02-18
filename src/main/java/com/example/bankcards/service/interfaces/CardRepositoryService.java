package com.example.bankcards.service.interfaces;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CardRepositoryService {
  CardEntity getById(UUID cardId);
  CardEntity getWithLock(UUID cardId);
  CardEntity save(CardEntity card);
  void delete(UUID cardId);
  void existsById(UUID cardId);
  void validateNotExistsByEncryptedNumber(String encryptedNumber);
  List<UUID> getAllIdList();
  List<UUID> findIdsByOwnerId(UUID ownerId);
  Page<CardEntity> pageableSearchById(UUID ownerId, Pageable pageable);
  Page<CardEntity> pageableSearchByIdAndStatus(UUID ownerId, CardStatus status, Pageable pageable);
}
