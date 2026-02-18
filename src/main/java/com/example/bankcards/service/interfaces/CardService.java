package com.example.bankcards.service.interfaces;

import com.example.bankcards.dto.BlockRequestResponse;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.CardBlockRequestEntity;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CardService {
  // операции со стороны администратора:
  CardResponse create(CreateCardRequest request);
  void block(UUID cardId);
  void activate(UUID cardId);
  void delete(UUID cardId);
  List<UUID> getAllCards();
  List<BlockRequestResponse> getAllBlockRequests();
  void approveBlock(UUID requestId);
  void rejectBlock(UUID requestId);

  // операции со стороны пользователя:
  List<UUID> getUserCardsIds(UUID userId);
  Page<CardResponse> findUserCards(UUID userId, Pageable pageable);
  Page<CardResponse> findUserCardsByStatus(UUID userId, CardStatus status, Pageable pageable);
  CardResponse getCardInfo(UUID cardId, UUID userId);
  CardBlockRequestEntity blockRequest(UUID cardId, UUID userId);
  BigDecimal getBalance(UUID cardId, UUID userId);
}
