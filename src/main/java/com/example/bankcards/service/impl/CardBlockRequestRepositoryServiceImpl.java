package com.example.bankcards.service.impl;

import com.example.bankcards.entity.CardBlockRequestEntity;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.service.interfaces.CardBlockRequestRepositoryService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CardBlockRequestRepositoryServiceImpl implements CardBlockRequestRepositoryService {
  private final CardBlockRequestRepository repository;

  public CardBlockRequestRepositoryServiceImpl(CardBlockRequestRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<CardBlockRequestEntity> getAll() {
    return repository.getAllInList();
  }

  @Override
  public CardBlockRequestEntity save(CardBlockRequestEntity entity) {
    return repository.save(entity);
  }

  @Override
  public CardBlockRequestEntity findByCardId(UUID cardId) {
    return repository.findByCardId(cardId)
            .orElseThrow(() -> new NotFoundException("There is no request with such card id!"));
  }
}
