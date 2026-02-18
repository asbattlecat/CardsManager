package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс, представляющий собой сущность карты
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "cards")
public class CardEntity {
  @Id private UUID id; // id карты

  @Column(nullable = false, unique = true) private String encryptedNumber; // зашифрованный номер

  @Column(nullable = false, length = 4)
  private String lastFourDigits; // последние четыре цифры карты

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity owner; // владелец

  @Column(nullable = false) private LocalDate expirationDate; // дата истечения срока

  @Column(nullable = false) @Enumerated(EnumType.STRING) private CardStatus status; // статус карты

  @Column(nullable = false) private BigDecimal balance; // баланс

  @Version private Long version;

  public CardEntity(String encryptedNumber, String lastFourDigits, UserEntity owner) {
    id = UUID.randomUUID();

    this.encryptedNumber = encryptedNumber;
    this.lastFourDigits = lastFourDigits;
    this.owner = owner;

    expirationDate = LocalDate.now().plusYears(5);

    status = CardStatus.ACTIVE;
    balance = BigDecimal.ZERO;
  }

  public String getMaskedNumber() {
    return "**** **** **** " + lastFourDigits;
  }
}
