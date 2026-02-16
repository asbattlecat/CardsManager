package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Класс, представляющий собой сущность карты
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "cards")
public class CardEntity {
  @Id
  private UUID id;

  @Column(nullable = false, unique = true)
  private String encryptedNumber;

  @Column(nullable = false, length = 4)
  private String lastFourDigits;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity owner;

  @Column(nullable = false)
  private BigDecimal balance;

  @Column(nullable = false)
  private LocalDate createdAt;

  @Column(nullable = false)
  private LocalDate expirationDate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CardStatus status;

  public CardEntity(String encryptedNumber, String lastFourDigits, UserEntity owner) {
    id = UUID.randomUUID();

    this.encryptedNumber = encryptedNumber;
    this.lastFourDigits = lastFourDigits;
    this.owner = owner;

    balance = BigDecimal.ZERO;
    createdAt = LocalDate.now();
    expirationDate = createdAt.plusYears(5);

    status = CardStatus.ACTIVE;
  }

  public String getMaskedNumber() {
    return "**** **** ****" + lastFourDigits;
  }
}
