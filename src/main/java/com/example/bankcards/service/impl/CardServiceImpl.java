package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.CardBlockRequestEntity;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.AlreadyExistsException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.EncryptionService;
import com.example.bankcards.service.interfaces.CardService;
import com.example.bankcards.util.CardCheckerUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CardServiceImpl implements CardService {
  private final CardRepository cardRepository;
  private final UserRepository userRepository;
  private final EncryptionService encryptionService;
  private final CardBlockRequestRepository blockRequestRepository;

  public CardServiceImpl(CardRepository cardRepository, UserRepository userRepository,
                         EncryptionService encryptionService, CardBlockRequestRepository blockRequestRepository) {
    this.cardRepository = cardRepository;
    this.userRepository = userRepository;
    this.encryptionService = encryptionService;
    this.blockRequestRepository = blockRequestRepository;
  }

  @Transactional
  @Override
  public CardResponse create(CreateCardRequest request, UUID userId) {
    Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
    if (userEntityOptional.isEmpty()) {
      throw new NotFoundException("There is no user with such Id!");
    }

    String encryptedNumber = encryptionService.encrypt(request.cardNumber());
    if (cardRepository.existsByEncryptedNumber(encryptedNumber)) {
      throw new AlreadyExistsException("Card with such number already exists!");
    }

    int cardNumberLength = request.cardNumber().length();
    String lastFourDigits = request.cardNumber().substring(cardNumberLength - 4);


    UserEntity user = userEntityOptional.get();
    CardEntity card = new CardEntity(encryptedNumber, lastFourDigits, user);

    cardRepository.save(card);

    return CardMapper.toDto(card);
  }

  @Transactional
  @Override
  public void block(UUID cardId) {
    Optional<CardEntity> cardEntityOptional = cardRepository.findById(cardId);
    CardCheckerUtil.checkCardExist(cardEntityOptional);

    CardEntity card = cardEntityOptional.get();

    CardCheckerUtil.checkCardStatus(card, CardStatus.BLOCKED);

    card.setStatus(CardStatus.BLOCKED);

    cardRepository.save(card);
  }

  @Transactional
  @Override
  public void activate(UUID cardId, UUID userId) {
    Optional<CardEntity> cardEntityOptional = cardRepository.findById(cardId);
    CardCheckerUtil.checkCardExist(cardEntityOptional);

    CardEntity card = cardEntityOptional.get();

    CardCheckerUtil.checkCardStatus(card, CardStatus.ACTIVE);

    card.setStatus(CardStatus.ACTIVE);

    cardRepository.save(card);
  }

  @Transactional
  @Override
  public void delete(UUID cardId) {
    checkCardExists(cardId);

    cardRepository.deleteById(cardId);
  }

  @Override
  public List<UUID> getAllCards() {
    return cardRepository.getAllIdList();
  }

  @Override
  public List<UUID> getUserCardsId(UUID userId) {
    checkUserExists(userId);
    return cardRepository.findIdsByOwnerId(userId);
  }

  @Override
  public CardResponse getCardInfo(UUID cardId) {
    Optional<CardEntity> cardEntityOptional = cardRepository.findById(cardId);
    CardCheckerUtil.checkCardExist(cardEntityOptional);

    CardEntity card = cardEntityOptional.get();

    return CardMapper.toDto(card);
  }

  @Override
  public void blockRequest(UUID cardId, UUID userId) {
    checkCardExists(cardId);
    checkUserExists(userId);

    CardBlockRequestEntity request = new CardBlockRequestEntity(cardId, userId);
    blockRequestRepository.save(request);
  }

  private void checkCardExists(UUID cardId) {
    if (!cardRepository.existsById(cardId)) {
      throw new NotFoundException("There is no card with such id!");
    }
  }

  private void checkUserExists(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new NotFoundException("There is no user with such id!");
    }
  }
}
