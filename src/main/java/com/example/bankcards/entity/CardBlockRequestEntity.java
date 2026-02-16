package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "card_block_request")
public class CardBlockRequestEntity {
  @Id
  private UUID id;

  @Column(nullable = false, unique = true)
  private UUID cardId;

  @Column(nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "request_status")
  private BlockRequestStatus requestStatus;

  public CardBlockRequestEntity(UUID cardId, UUID userId) {
    id = UUID.randomUUID();

    this.cardId = cardId;
    this.userId = userId;

    requestStatus = BlockRequestStatus.PENDING;
  }

  public void changeStatus(BlockRequestStatus status) {
    this.requestStatus = status;
  }
}
