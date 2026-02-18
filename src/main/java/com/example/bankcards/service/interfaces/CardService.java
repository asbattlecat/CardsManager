package com.example.bankcards.service.interfaces;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CardService {
  // операции со стороны администратора:
  CardResponse create(CreateCardRequest request, UUID userId);
  void block(UUID cardId);
  void activate(UUID cardId);
  void delete(UUID cardId);
  List<UUID> getAllCards();

  // операции со стороны пользователя:
  List<UUID> getUserCardsIds(UUID userId);
  CardResponse getCardInfo(UUID cardId);
  void blockRequest(UUID cardId, UUID userId);
}
