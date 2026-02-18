package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.BlockRequestStatus;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "card_block_request")
public class CardBlockRequestEntity {
  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false, name = "card_id")
  private CardEntity card;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false, name = "owner_id")
  private UserEntity owner;

  @Enumerated(EnumType.STRING)
  @Column(name = "request_status")
  private BlockRequestStatus requestStatus;

  public CardBlockRequestEntity(CardEntity card, UserEntity owner) {
    id = UUID.randomUUID();

    this.card = card;
    this.owner = owner;

    requestStatus = BlockRequestStatus.PENDING;
  }

  public void changeStatus(BlockRequestStatus status) {
    this.requestStatus = status;
  }

  public void approve() {
    requestStatus = BlockRequestStatus.APPROVED;
  }

  public void reject() {
    requestStatus = BlockRequestStatus.REJECTED;
  }
}
