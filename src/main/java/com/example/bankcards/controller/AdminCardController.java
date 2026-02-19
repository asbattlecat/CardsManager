package com.example.bankcards.controller;

import com.example.bankcards.dto.BlockRequestResponse;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.service.interfaces.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Управление картами (админ)", description = "API для административного управления банковскими картами")
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
  @Operation(
          summary = "Создание новой карты",
          description = "Создает банковскую карту для указанного пользователя. Требуются права администратора."
  )
  @PostMapping()
  public ResponseEntity<CardResponse> create(@Valid @RequestBody CreateCardRequest request) {
    return ResponseEntity.ok(cardService.create(request));
  }

  /**
   * Удаление карты
   * @param cardId ID удаляемой карты
   * @return статус проведенной операции
   */
  @Operation(
          summary = "Удаление карты пользователя",
          description = "Удаляет указанную карту. Требуются права администратора."
  )
  @DeleteMapping("/{cardId}")
  public ResponseEntity<?> delete(@PathVariable UUID cardId) {
    cardService.delete(cardId);
    return ResponseEntity.ok().build();
  }

  @Operation(
          summary = "Блокировка карты пользователя",
          description = "Блокирует указанную карту. Требуются права администратора."
  )
  @PatchMapping("/{cardId}/block")
  public ResponseEntity<?> block(@PathVariable UUID cardId) {
    cardService.block(cardId);
    return ResponseEntity.ok().build();
  }

  @Operation(
          summary = "Активация карты пользователя",
          description = "Активирует указанную карту. Требуются права администратора."
  )
  @PatchMapping("/{cardId}/activate")
  public ResponseEntity<?> activate(@PathVariable UUID cardId) {
    cardService.activate(cardId);
    return ResponseEntity.ok().build();
  }

  @Operation(
          summary = "Получение списка всех карт",
          description = "Получение списка всех карт, список из ID карт (UUID). Требуются права администратора."
  )
  @GetMapping("/allCards")
  public ResponseEntity<List<UUID>> allCards() {
    return ResponseEntity.ok(cardService.getAllCards());
  }

  @Operation(
          summary = "Информация о карте",
          description = "Получение информации о карте по ее ID. Требуются права администратора."
  )
  @GetMapping("/{cardId}")
  public ResponseEntity<CardResponse> cardInfo(@PathVariable UUID cardId,
                                               @AuthenticationPrincipal UserEntity user) {
    return ResponseEntity.ok(cardService.getCardInfo(cardId, user.getId()));
  }

  @Operation(
          summary = "Информация о всех запросах на блокировку карт",
          description = "Получение информации о всех запросах на блокировку, списком. Требуются права администратора."
  )
  @GetMapping("/all-block-requests")
  public ResponseEntity<List<BlockRequestResponse>> allBlockRequests() {
    return ResponseEntity.ok(cardService.getAllBlockRequests());
  }

  @Operation(
          summary = "Одобрение запроса на блокировку",
          description = "Операция одобрения запроса на блокировку указанной карты. Требуются права администратора."
  )
  @PatchMapping("/approve-block/{requestId}")
  public ResponseEntity<?> approveBlock(@PathVariable UUID requestId) {
    cardService.approveBlock(requestId);
    return ResponseEntity.ok().build();
  }

  @Operation(
          summary = "Отклонение запроса на блокировку",
          description = "Операция отклонения запроса на блокировку указанной карты. Требуются права администратора."
  )
  @PatchMapping("/reject-block/{requestId}")
  public ResponseEntity<?> rejectBlock(@PathVariable UUID requestId) {
    cardService.rejectBlock(requestId);
    return ResponseEntity.ok().build();
  }

}
