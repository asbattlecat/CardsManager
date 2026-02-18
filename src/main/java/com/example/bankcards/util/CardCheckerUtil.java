package com.example.bankcards.util;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.NotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.stereotype.Component;

public class CardCheckerUtil {
  /**
   * Проверяет, что карта заблокирована
   * @param card объект сущности CardEntity
   */
  public static void checkCardBlocked(CardEntity card) {
    if (!card.getStatus().equals(CardStatus.BLOCKED)) {
      throw new IllegalStateException("Only blocked card can be used for this operation!");
    }
  }

  /**
   * Проверяет, что карта активна
   * @param card объект сущности CardEntity
   */
  public static void checkCardActive(CardEntity card) {
    if (!card.getStatus().equals(CardStatus.ACTIVE)) {
      throw new IllegalStateException("Only active card can be used for this operation!");
    }
  }
}
