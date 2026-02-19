package com.example.bankcards.controller;

import com.example.bankcards.dto.BlockRequestResponse;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.service.interfaces.CardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/cards")
public class AdminCardController {
  private final CardService cardService;

  public AdminCardController(CardService cardService) {
    this.cardService = cardService;
  }

  /**
   * Создание карты
   * @param request реквест для создания карты
   * @return информацию о созданной карте
   */
  @PostMapping()
  public ResponseEntity<CardResponse> create(@Valid @RequestBody CreateCardRequest request) {
    return ResponseEntity.ok(cardService.create(request));
  }

  /**
   * Удаление карты
   * @param cardId ID удаляемой карты
   * @return статус проведенной операции
   */
  @DeleteMapping("/{cardId}")
  public ResponseEntity<?> delete(@PathVariable UUID cardId) {
    cardService.delete(cardId);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{cardId}/block")
  public ResponseEntity<?> block(@PathVariable UUID cardId) {
    cardService.block(cardId);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{cardId}/activate")
  public ResponseEntity<?> activate(@PathVariable UUID cardId) {
    cardService.activate(cardId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/allCards")
  public ResponseEntity<List<UUID>> allCards() {
    return ResponseEntity.ok(cardService.getAllCards());
  }

  @GetMapping("/{cardId}")
  public ResponseEntity<CardResponse> cardInfo(@PathVariable UUID cardId,
                                               @AuthenticationPrincipal UserEntity user) {
    return ResponseEntity.ok(cardService.getCardInfo(cardId, user.getId()));
  }

  @GetMapping("/all-block-requests")
  public ResponseEntity<List<BlockRequestResponse>> allBlockRequests() {
    return ResponseEntity.ok(cardService.getAllBlockRequests());
  }

  @PatchMapping("/approve-block/{requestId}")
  public ResponseEntity<?> approveBlock(@PathVariable UUID requestId) {
    cardService.approveBlock(requestId);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/reject-block/{requestId}")
  public ResponseEntity<?> rejectBlock(@PathVariable UUID requestId) {
    cardService.rejectBlock(requestId);
    return ResponseEntity.ok().build();
  }

}
