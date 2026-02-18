package com.example.bankcards.service.interfaces;

import com.example.bankcards.entity.CardEntity;

import java.util.List;
import java.util.UUID;

public interface CardRepositoryService {
  CardEntity get(UUID cardId);
  CardEntity getWithLock(UUID cardId);
  CardEntity save(CardEntity card);
  void delete(UUID cardId);
  void existsById(UUID cardId);
  void validateNotExistsByEncryptedNumber(String encryptedNumber);
  List<UUID> getAllIdList();
  List<UUID> findIdsByOwnerId(UUID ownerId);
}
