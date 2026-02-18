package com.example.bankcards.service.impl;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.exception.InvalidIdException;
import com.example.bankcards.service.interfaces.CardRepositoryService;
import com.example.bankcards.service.interfaces.TransactionService;
import com.example.bankcards.service.interfaces.UserRepositoryService;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {
  private final CardRepositoryService cardRepositoryService;
  private final UserRepositoryService userRepositoryService;

  public TransactionServiceImpl(
      CardRepositoryService cardRepositoryService, UserRepositoryService userRepositoryService) {
    this.cardRepositoryService = cardRepositoryService;
    this.userRepositoryService = userRepositoryService;
  }

  @Transactional
  @Override
  public void transfer(UUID userId, UUID from, UUID to, BigDecimal transferAmount) {
    validateTransferRequest(userId, from, to, transferAmount);

    // мешура во избежание deadlock (всегда один порядок блокировок)
    List<UUID> allIds = Arrays.asList(from, to);
    Collections.sort(allIds);

    CardEntity firstCard = cardRepositoryService.getWithLock(allIds.get(0));
    CardEntity secondCard = cardRepositoryService.getWithLock(allIds.get(1));

    validateCardOwner(userId, firstCard, secondCard);

    CardEntity fromEntity = firstCard.getId().equals(from) ? firstCard : secondCard;
    CardEntity toEntity = firstCard.getId().equals(from) ? secondCard : firstCard;

    validateFromBalance(fromEntity, transferAmount);

    fromEntity.setBalance(fromEntity.getBalance().subtract(transferAmount));
    toEntity.setBalance(toEntity.getBalance().add(transferAmount));

    cardRepositoryService.save(fromEntity);
    cardRepositoryService.save(toEntity);
  }

  /**
   * Начальная валидация перевода денег. <p>
   * Этапы: <p>
   *   1 этап: значение перевода больше нуля <p>
   *   2 этап: владелец карты существует в БД <p>
   *   3 этап: перевод не происходит на ту же самую карту, с которой списываются деньги
   * @param userId ID владельца карты
   * @param from ID карты, с которой происходит списание
   * @param to ID карты, на которую будет происходить перевод
   * @param transferAmount значение перевода
   */
  private void validateTransferRequest(UUID userId, UUID from, UUID to, BigDecimal transferAmount) {
    if (transferAmount == null || transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be positive!");
    }

    userRepositoryService.validateExistsById(userId);

    // нельзя переводить деньги на ту же карту, с которой списываешь
    if (from.equals(to)) {
      throw new InvalidIdException("Cannot transfer money between same cards!");
    }
  }

  /**
   * Проверка владельца карт для переводов. Это нужно, т.к. в тз описана возможность перевода только
   * между картами одного владельца
   * @param userId ID владельца карты
   * @param fromEntity объект карты, с которой происходит списание
   * @param toEntity объект карты, на которую происходит перевод
   */
  private void validateCardOwner(UUID userId, CardEntity fromEntity, CardEntity toEntity) {
    UUID fromOwnerId = fromEntity.getOwner().getId();
    UUID toOwnerId = toEntity.getOwner().getId();
    // так как переводы разрешены только между картами одного пользователя, это проверка на то, что
    // у указанных карт совпадают владельцы, и владелец равен userId (аргумент)

    if (!fromOwnerId.equals(toOwnerId) || !fromOwnerId.equals(userId)) {
      throw new InvalidIdException("One or both cards do not belong to the specified user!");
    }
  }

  /**
   * Проверка того, что сумма списания не больше баланса карты, с которой происходит списание
   * @param fromEntity объект карты, с которой происходит списание
   * @param transferAmount значение перевода
   */
  private void validateFromBalance(CardEntity fromEntity, BigDecimal transferAmount) {
    // на балансе недостаточно денег для перевода
    if (fromEntity.getBalance().compareTo(transferAmount) < 0) {
      throw new InsufficientFundsException("Not enough money!");
    }
  }
}
