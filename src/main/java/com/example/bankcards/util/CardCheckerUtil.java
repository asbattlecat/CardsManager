package com.example.bankcards.util;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.NotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class CardCheckerUtil {
  /**
   * Проверяет, что <code>card</code> не пустой (в репозитории есть такая карта)
   * @param card получен из репозитория <code>CardRepository</code>
   */
  public static void checkCardExist(Optional<CardEntity> card) {
    if (card.isEmpty()) {
      throw new NotFoundException("There is no card with such id!!");
    }
  }

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
