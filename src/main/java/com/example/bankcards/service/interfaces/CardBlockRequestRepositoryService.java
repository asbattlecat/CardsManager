package com.example.bankcards.service.interfaces;

import com.example.bankcards.entity.CardBlockRequestEntity;

import java.util.List;
import java.util.UUID;

public interface CardBlockRequestRepositoryService {
  List<CardBlockRequestEntity> getAll();
  CardBlockRequestEntity save(CardBlockRequestEntity entity);
  CardBlockRequestEntity findByCardId(UUID ownerId);
}
