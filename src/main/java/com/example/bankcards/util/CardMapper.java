package com.example.bankcards.util;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.dto.CardResponse;

public class CardMapper {
  public static CardResponse toDto(CardEntity cardEntity) {
    return new CardResponse(
            cardEntity.getId(),
            cardEntity.getMaskedNumber(),
            cardEntity.getBalance(),
            cardEntity.getExpirationDate(),
            cardEntity.getStatus()
    );
  }
}
