package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.interfaces.CardService;
import com.example.bankcards.service.interfaces.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/user/cards")
public class UserCardController {
  private final CardService cardService;
  private final TransactionService transactionService;

  public UserCardController(CardService cardService, TransactionService transactionService) {
    this.cardService = cardService;
    this.transactionService = transactionService;
  }

  @GetMapping("/all")
  public ResponseEntity<List<UUID>> allCards(@AuthenticationPrincipal UserEntity user) {
    return ResponseEntity.ok(cardService.getUserCardsIds(user.getId()));
  }

  @GetMapping
  public ResponseEntity<Page<CardResponse>> getUserCard(
          @AuthenticationPrincipal UserEntity user,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(required = false) CardStatus status
          ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("expirationDate").ascending());

    Page<CardResponse> cards;
    if (status != null) {
      cards = cardService.findUserCardsByStatus(user.getId(), status, pageable);
    } else {
      cards = cardService.findUserCards(user.getId(), pageable);
    }

    return ResponseEntity.ok(cards);
  }

  @GetMapping("/{cardId}")
  public ResponseEntity<BigDecimal> cardBalance(
          @PathVariable UUID cardId,
          @AuthenticationPrincipal UserEntity user) {
    return ResponseEntity.ok(cardService.getBalance(cardId, user.getId()));
  }

  @PatchMapping("/transfer")
  public ResponseEntity<?> transfer(
          @RequestBody TransferRequest request,
          @AuthenticationPrincipal UserEntity user) {
    transactionService.transfer(user.getId(), request.from(), request.to(), request.amount());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/blockRequest")
  public ResponseEntity<?> blockRequest() {

  }
}
