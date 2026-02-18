package com.example.bankcards.util;

import com.example.bankcards.entity.CardBlockRequestEntity;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;

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

  public static void checkBlockRequestPending(CardBlockRequestEntity request) {
    if (!request.getRequestStatus().equals(BlockRequestStatus.PENDING)) {
      throw new IllegalStateException("Block request is already processed!");
    }
  }
}
