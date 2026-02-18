package com.example.bankcards.service.interfaces;

import com.example.bankcards.entity.CardBlockRequestEntity;

import java.util.List;
import java.util.UUID;

public interface CardBlockRequestRepositoryService {
  List<CardBlockRequestEntity> getAllInList();
  CardBlockRequestEntity save(CardBlockRequestEntity entity);
  CardBlockRequestEntity findById(UUID requestId);
  void validateNotExistsByCardId(UUID cardId);
}
