package com.example.bankcards.service.impl;

import com.example.bankcards.entity.CardBlockRequestEntity;
import com.example.bankcards.exception.AlreadyExistsException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.service.interfaces.CardBlockRequestRepositoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CardBlockRequestRepositoryServiceImpl implements CardBlockRequestRepositoryService {
  private final CardBlockRequestRepository repository;

  public CardBlockRequestRepositoryServiceImpl(CardBlockRequestRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<CardBlockRequestEntity> getAllInList() {
    return repository.getAllInList();
  }

  @Override
  public CardBlockRequestEntity save(CardBlockRequestEntity entity) {
    return repository.save(entity);
  }

  @Override
  public CardBlockRequestEntity findById(UUID requestId) {
    return repository.findById(requestId)
            .orElseThrow(() -> new NotFoundException("There is no request with such card id!"));
  }

  @Override
  public void validateNotExistsByCardId(UUID cardId) {
    if (repository.existsByCardId(cardId)) {
      throw new AlreadyExistsException("Block request already exists!");
    }
  }
}
