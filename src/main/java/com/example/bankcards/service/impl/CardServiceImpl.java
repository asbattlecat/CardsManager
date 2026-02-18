package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.CardBlockRequestEntity;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.security.interfaces.EncryptionService;
import com.example.bankcards.service.interfaces.CardRepositoryService;
import com.example.bankcards.service.interfaces.CardService;
import com.example.bankcards.service.interfaces.UserRepositoryService;
import com.example.bankcards.util.CardCheckerUtil;
import com.example.bankcards.util.CardMapper;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardServiceImpl implements CardService {
  private final CardRepositoryService cardRepositoryService;
  private final UserRepositoryService userRepositoryService;
  private final EncryptionService encryptionService;
  private final CardBlockRequestRepository blockRequestRepository;

  public CardServiceImpl(CardRepositoryService cardRepositoryService,
      UserRepositoryService userRepositoryService, EncryptionService encryptionService,
      CardBlockRequestRepository blockRequestRepository) {
    this.cardRepositoryService = cardRepositoryService;
    this.userRepositoryService = userRepositoryService;
    this.encryptionService = encryptionService;
    this.blockRequestRepository = blockRequestRepository;
  }

  @Transactional
  @Override
  public CardResponse create(CreateCardRequest request, UUID userId) {
    UserEntity user = userRepositoryService.get(userId);

    String encryptedNumber = encryptionService.encrypt(request.cardNumber());
    cardRepositoryService.validateNotExistsByEncryptedNumber(encryptedNumber);

    int cardNumberLength = request.cardNumber().length();
    String lastFourDigits = request.cardNumber().substring(cardNumberLength - 4);

    CardEntity card = new CardEntity(encryptedNumber, lastFourDigits, user);

    cardRepositoryService.save(card);

    return CardMapper.toDto(card);
  }

  @Transactional
  @Override
  public void block(UUID cardId) {
    CardEntity card = cardRepositoryService.get(cardId);

    // только активная карта может быть заблокирована
    CardCheckerUtil.checkCardActive(card);

    card.setStatus(CardStatus.BLOCKED);

    cardRepositoryService.save(card);
  }

  @Transactional
  @Override
  public void activate(UUID cardId) {
    CardEntity card = cardRepositoryService.get(cardId);

    // только заблокированная карта может быть активирована
    CardCheckerUtil.checkCardBlocked(card);

    card.setStatus(CardStatus.ACTIVE);

    cardRepositoryService.save(card);
  }

  @Transactional
  @Override
  public void delete(UUID cardId) {
    cardRepositoryService.existsById(cardId);
    cardRepositoryService.delete(cardId);
  }

  @Transactional(readOnly = true)
  @Override
  public List<UUID> getAllCards() {
    return cardRepositoryService.getAllIdList();
  }

  @Transactional(readOnly = true)
  @Override
  public List<UUID> getUserCardsIds(UUID userId) {
    userRepositoryService.existsById(userId);
    return cardRepositoryService.findIdsByOwnerId(userId);
  }

  @Transactional(readOnly = true)
  @Override
  public CardResponse getCardInfo(UUID cardId) {
    CardEntity card = cardRepositoryService.get(cardId);

    return CardMapper.toDto(card);
  }

  @Transactional
  @Override
  public void blockRequest(UUID cardId, UUID userId) {
    cardRepositoryService.existsById(cardId);
    userRepositoryService.existsById(userId);

    CardBlockRequestEntity request = new CardBlockRequestEntity(cardId, userId);
    blockRequestRepository.save(request);
  }
}
