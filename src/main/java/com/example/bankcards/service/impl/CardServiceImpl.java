package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.CardNumberIsBusyException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.EncryptionService;
import com.example.bankcards.service.interfaces.CardService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CardServiceImpl implements CardService {
  private final CardRepository cardRepository;
  private final UserRepository userRepository;
  private final EncryptionService encryptionService;

  public CardServiceImpl(CardRepository cardRepository, UserRepository userRepository,
                         EncryptionService encryptionService) {
    this.cardRepository = cardRepository;
    this.userRepository = userRepository;
    this.encryptionService = encryptionService;
  }

  @Override
  public CardResponse create(CreateCardRequest request, UUID userId) {
    Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
    if (userEntityOptional.isEmpty()) {
      throw new UserNotFoundException("There is no user with such Id!");
    }

    String encryptedNumber = encryptionService.encrypt(request.cardNumber());
    if (cardRepository.existsByEncryptedNumber(encryptedNumber)) {
      throw new CardNumberIsBusyException("Card with such number already exists!");
    }

    int cardNumberLength = request.cardNumber().length();
    String lastFourDigits = request.cardNumber().substring(cardNumberLength - 4);


    UserEntity user = userEntityOptional.get();
    CardEntity card = new CardEntity(encryptedNumber, lastFourDigits, user);

    cardRepository.save(card);

    return CardMapper.toDto(card);
  }

  @Override
  public void delete(UUID cardId, UUID userId) {

  }

  @Override
  public void block(UUID cardId, UUID userId) {

  }

  @Override
  public void activate(UUID cardId, UUID userId) {

  }

  @Override
  public List<UUID> getAllCards(UUID userId) {
    return List.of();
  }

  @Override
  public List<UUID> getUserCardsId(UUID userId) {
    return List.of();
  }

  @Override
  public CardResponse getCardInfo(UUID cardId) {
    return null;
  }

  @Override
  public void blockRequest(UUID cardId, UUID userId) {

  }
}
