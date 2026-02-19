package com.example.bankcards.service.impl;

import com.example.bankcards.dto.BlockRequestResponse;
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

import jakarta.validation.Valid;
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

    card.block();

    cardRepositoryService.save(card);
  }

  @Transactional
  @Override
  public void activate(UUID cardId) {
    CardEntity card = cardRepositoryService.getById(cardId);

    // только заблокированная карта может быть активирована
    CardCheckerUtil.checkCardBlocked(card);

    card.activate();

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
  public List<BlockRequestResponse> getAllBlockRequests() {

    return blockRequestRepositoryService.getAllInList().stream()
            .map(CardMapper::toDto)
            .toList();
  }

  @Transactional
  @Override
  public void approveBlock(UUID requestId) {
    CardBlockRequestEntity request = blockRequestRepositoryService.findById(requestId);
    CardCheckerUtil.checkBlockRequestPending(request);

    CardEntity card = request.getCard();
    CardCheckerUtil.checkCardActive(card);
    request.approve();

    blockRequestRepositoryService.save(request);
  }

  @Transactional
  @Override
  public void rejectBlock(UUID requestId) {
    CardBlockRequestEntity request = blockRequestRepositoryService.findById(requestId);
    CardCheckerUtil.checkBlockRequestPending(request);

    CardEntity card = request.getCard();
    CardCheckerUtil.checkCardActive(card);
    request.reject();

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

    // операцию может проводить либо владелец карты, либо админ
    checkOwnerOrAdmin(card, userEntity);
    return CardMapper.toDto(card);
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

    blockRequestRepositoryService.validateNotExistsByCardId(cardId);
    CardBlockRequestEntity request = new CardBlockRequestEntity(card, owner);
    return blockRequestRepositoryService.save(request);
  }

  @Transactional(readOnly = true)
  @Override
  public BigDecimal getBalance(UUID cardId, UUID userId) {
    UserEntity user = userRepositoryService.getById(userId);
    CardEntity card = cardRepositoryService.getById(cardId);

    // операцию может проводить либо владелец карты, либо админ
    checkOwnerOrAdmin(card, user);

    return card.getBalance();
  }


  private void checkOwnerOrAdmin(CardEntity card, UserEntity user) {
    if (!(user.getId().equals(card.getOwner().getId()) || user.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))) {
      throw new InvalidIdException("User is not owner or is not admin!");
    }
  }
}
