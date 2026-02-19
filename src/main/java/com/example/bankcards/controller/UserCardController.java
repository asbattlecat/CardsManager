package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.interfaces.CardService;
import com.example.bankcards.service.interfaces.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/cards")
@Tag(name = "Операции над картами со стороны пользователя", description = "API просмотра списка всех карт, пагинация " +
        "по картам, расположение запроса на блокировку карты пользователем, баланс по карте, и перевод между своими " +
        "картами пользователя")
public class UserCardController {
  private final CardService cardService;
  private final TransactionService transactionService;

  public UserCardController(CardService cardService, TransactionService transactionService) {
    this.cardService = cardService;
    this.transactionService = transactionService;
  }

  @Operation(
          summary = "Получение списка всех карт пользователя",
          description = "Получаем список ID всех карт пользователя, UUID"
  )
  @GetMapping("/all")
  public ResponseEntity<List<UUID>> allUsersCards(@AuthenticationPrincipal UserEntity user) {
    return ResponseEntity.ok(cardService.getUserCardsIds(user.getId()));
  }

  @Operation(
          summary = "Получение списка всех карт пользователя, пагинация",
          description = "Получаем страницы информации о картах"
  )
  @GetMapping
  public ResponseEntity<Page<CardResponse>> getUserCardPagination(
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

  @Operation(
          summary = "Размещение запроса на блокировку карты пользователем"
  )
  @PostMapping("/block-request/{cardId}")
  public ResponseEntity<?> blockRequest(
          @AuthenticationPrincipal UserEntity user,
          @PathVariable UUID cardId
  ) {
    cardService.blockRequest(cardId, user.getId());
    return ResponseEntity.ok().build();
  }

  @Operation(
          summary = "Баланс карты",
          description = "Получаем информацию о балансе карты по ее ID"
  )
  @GetMapping("/{cardId}")
  public ResponseEntity<BigDecimal> cardBalance(
          @PathVariable UUID cardId,
          @AuthenticationPrincipal UserEntity user) {
    return ResponseEntity.ok(cardService.getBalance(cardId, user.getId()));
  }

  @Operation(
          summary = "Перевод между картами пользователя",
          description = "Операция перевода между картами одного пользователя, указываем откуда, куда, и баланс"
  )
  @PatchMapping("/transfer")
  public ResponseEntity<?> transfer(
          @Valid
          @RequestBody TransferRequest request,
          @AuthenticationPrincipal UserEntity user) {
    transactionService.transfer(user.getId(), request.from(), request.to(), request.amount());
    return ResponseEntity.ok().build();
  }
}
