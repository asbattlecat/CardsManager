package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.CardBlockRequestEntity;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.InvalidIdException;
import com.example.bankcards.security.interfaces.EncryptionService;
import com.example.bankcards.service.interfaces.CardBlockRequestRepositoryService;
import com.example.bankcards.service.interfaces.CardRepositoryService;
import com.example.bankcards.service.interfaces.CardService;
import com.example.bankcards.service.interfaces.UserRepositoryService;
import com.example.bankcards.util.CardCheckerUtil;
import com.example.bankcards.util.CardMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardServiceImpl implements CardService {
  private final CardRepositoryService cardRepositoryService;
  private final UserRepositoryService userRepositoryService;
  private final EncryptionService encryptionService;
  private final CardBlockRequestRepositoryService blockRequestRepositoryService;

  public CardServiceImpl(CardRepositoryService cardRepositoryService,
      UserRepositoryService userRepositoryService, EncryptionService encryptionService,
      CardBlockRequestRepositoryService blockRequestRepositoryService) {
    this.cardRepositoryService = cardRepositoryService;
    this.userRepositoryService = userRepositoryService;
    this.encryptionService = encryptionService;
    this.blockRequestRepositoryService = blockRequestRepositoryService;
  }

  @Transactional
  @Override
  public CardResponse create(CreateCardRequest request) {
    UserEntity user = userRepositoryService.getById(request.userId());

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
    CardEntity card = cardRepositoryService.getById(cardId);

    // только активная карта может быть заблокирована
    CardCheckerUtil.checkCardActive(card);

    card.setStatus(CardStatus.BLOCKED);

    cardRepositoryService.save(card);
  }

  @Transactional
  @Override
  public void activate(UUID cardId) {
    CardEntity card = cardRepositoryService.getById(cardId);

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

  @Override
  public List<UUID> getBlockRequests() {
    return List.of();
  }

  @Transactional
  @Override
  public void approveBlock(UUID cardId) {
    CardEntity card = cardRepositoryService.getById(cardId);
    CardCheckerUtil.checkCardActive(card);
    CardBlockRequestEntity request = blockRequestRepositoryService.findByCardId(cardId);
    CardCheckerUtil.checkBlockRequestPending(request);

    card.setStatus(CardStatus.BLOCKED);
    request.setRequestStatus(BlockRequestStatus.APPROVED);

    cardRepositoryService.save(card);
    blockRequestRepositoryService.save(request);
  }

  @Transactional
  @Override
  public void rejectBlock(UUID cardId) {
    CardEntity card = cardRepositoryService.getById(cardId);
    CardCheckerUtil.checkCardActive(card);
    CardBlockRequestEntity request = blockRequestRepositoryService.findByCardId(cardId);
    CardCheckerUtil.checkBlockRequestPending(request);

    request.setRequestStatus(BlockRequestStatus.REJECTED);

    blockRequestRepositoryService.save(request);
  }

  @Transactional(readOnly = true)
  @Override
  public List<UUID> getUserCardsIds(UUID userId) {
    userRepositoryService.validateExistsById(userId);
    return cardRepositoryService.findIdsByOwnerId(userId);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<CardResponse> findUserCards(UUID userId, Pageable pageable) {
    return cardRepositoryService.pageableSearchById(userId, pageable)
            .map(CardMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<CardResponse> findUserCardsByStatus(UUID userId, CardStatus status, Pageable pageable) {
    return cardRepositoryService.pageableSearchByIdAndStatus(userId, status, pageable)
            .map(CardMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public CardResponse getCardInfo(UUID cardId, UUID userId) {
    CardEntity card = cardRepositoryService.getById(cardId);

    UserEntity userEntity = userRepositoryService.getById(userId);
    UUID userEntityId = userEntity.getId();

    if (userEntityId == card.getOwner().getId() || userEntity.getAuthorities().contains(Role.ADMIN)) {
      return CardMapper.toDto(card);
    } else {
      throw new InvalidIdException("User is not owner or is not admin!");
    }

  }

  @Transactional
  @Override
  public CardBlockRequestEntity blockRequest(UUID cardId, UUID userId) {
    CardEntity card = cardRepositoryService.getById(cardId);
    CardCheckerUtil.checkCardActive(card);

    UserEntity owner = userRepositoryService.getById(userId);

    if (!card.getOwner().getId().equals(owner.getId())) {
      throw new InvalidIdException("User is not owner of this card!");
    }

    CardBlockRequestEntity request = new CardBlockRequestEntity(card, owner);
    return blockRequestRepositoryService.save(request);
  }

  @Transactional(readOnly = true)
  @Override
  public BigDecimal getBalance(UUID cardId, UUID userId) {
    userRepositoryService.validateExistsById(userId);

    CardEntity card = cardRepositoryService.getById(cardId);
    return card.getBalance();
  }
}
