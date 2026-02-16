package com.example.bankcards.util;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.NotFoundException;

import java.util.Optional;

public class CardCheckerUtil {
  /**
   * Метод, проверяющий, что <code>card</code> не пустой
   * @param card получен из репозитория <code>CardRepository</code>
   */
  public static void checkCardExist(Optional<CardEntity> card) {
    if (card.isEmpty()) {
      throw new NotFoundException("There is no card with such id!!");
    }
  }

  /**
   * Проверят, что у объекта <code>card</code> CardStatus не совпадает со статусом объекта <code>status</code>
   * @param card объект, полученный из репозитория <code>CardRepository</code>
   * @param status сверяемый статус
   */
  public static void checkCardStatus(CardEntity card, CardStatus status) {
    if (card.getStatus().equals(status)) {
      throw new IllegalStateException("This status is already set!");
    }
  }
}
