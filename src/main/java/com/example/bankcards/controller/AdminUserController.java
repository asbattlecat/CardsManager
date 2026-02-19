package com.example.bankcards.controller;

import com.example.bankcards.service.interfaces.UserRepositoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Управление пользователями (админ)", description = "API для административного управления пользователями")
public class AdminUserController {
  private final UserRepositoryService userRepositoryService;

  public AdminUserController(UserRepositoryService userRepositoryService) {
    this.userRepositoryService = userRepositoryService;
  }

  @Operation(
          summary = "Удаление пользователей",
          description = "Удаляет указанного пользователя по ID (UUID). Требуются права администратора."
  )
  @Transactional
  @DeleteMapping("/{userId}")
  public ResponseEntity<?> deleteUser(@Valid @NotNull @PathVariable UUID userId) {
    userRepositoryService.delete(userId);
    return ResponseEntity.ok().build();
  }
}
