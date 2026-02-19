package com.example.bankcards.controller;

import com.example.bankcards.dto.JwtRequest;
import com.example.bankcards.dto.JwtResponse;
import com.example.bankcards.dto.SignupRequest;
import com.example.bankcards.dto.TokenRefreshRequest;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Операции по регистрации и логину", description = "API авторизации, регистрации, и создания новых токенов" +
        "обновления и доступа")
public class AuthorizationController {
  private final AuthService userService;

  public AuthorizationController(AuthService userService) {
    this.userService = userService;
  }

  @Operation(
          summary = "Логин пользователя",
          description = "Вход в систему с помощью email и password. Не требует прав."
  )
  @PostMapping("/user/login")
  public ResponseEntity<JwtResponse> login(@Valid @RequestBody JwtRequest request) {
    JwtResponse response = userService.login(request);
    return ResponseEntity.ok(response);
  }

  @Operation(
          summary = "Регистрация пользователя",
          description = "Регистрация пользователя по email, password и роли (USER/ADMIN). Требует прав администрации"
  )
  @PostMapping("/admin/users/register")
  public ResponseEntity<?> register(@Valid @RequestBody SignupRequest request) {
    userService.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(
          summary = "Регистрация первого админа в систему",
          description = "Регистрация первого админа в систему. Не требует прав доступа"
  )
  @PostMapping("/setup/first-admin")
  public ResponseEntity<?> firstAdmin() {
    userService.signup(new SignupRequest("admin", "admin", Role.ADMIN));
    return ResponseEntity.ok().build();
  }

  @Operation(
          summary = "Обновление пары токенов (обновление, доступа)",
          description = "Обновление пары токенов (обновление, доступа). Требует прав пользователя"
  )
  @PostMapping("/user/refresh-tokens")
  public ResponseEntity<JwtResponse> refreshTokens(@Valid @RequestBody TokenRefreshRequest request) {
    userService.refresh(request.refreshToken());
    return ResponseEntity.ok().build();
  }
}
