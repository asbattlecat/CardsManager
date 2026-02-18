package com.example.bankcards.service.impl;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.exception.AlreadyExistsException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.interfaces.CardRepositoryService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CardRepositoryServiceImpl implements CardRepositoryService {
  private final CardRepository cardRepository;

  public CardRepositoryServiceImpl(CardRepository cardRepository) {
    this.cardRepository = cardRepository;
  }

  @Override
  public CardEntity get(UUID cardId) {
    return cardRepository.findById(cardId).orElseThrow(
        () -> new NotFoundException("There is no card with such id!"));
  }

  @Override
  public CardEntity getWithLock(UUID cardId) {
    return cardRepository.findByIdWithPessimisticLock(cardId).orElseThrow(
        () -> new NotFoundException("There is no card with such id!"));
  }

  @Override
  public CardEntity save(CardEntity card) {
    return cardRepository.save(card);
  }

  @Override
  public void delete(UUID cardId) {
    cardRepository.deleteById(cardId);
  }

  @Override
  public void existsById(UUID cardId) {
    if (!cardRepository.existsById(cardId)) {
      throw new NotFoundException("There is no card with such id!");
    }
  }

  @Override
  public void validateNotExistsByEncryptedNumber(String encryptedNumber) {
    if (cardRepository.existsByEncryptedNumber(encryptedNumber)) {
      throw new AlreadyExistsException("Card with such number already exists!");
    }
  }

  @Override
  public List<UUID> getAllIdList() {
    return cardRepository.getAllIdList();
  }

  @Override
  public List<UUID> findIdsByOwnerId(UUID ownerId) {
    return cardRepository.findIdsByOwnerId(ownerId);
  }
}
