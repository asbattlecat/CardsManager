package com.example.bankcards.controller;

import com.example.bankcards.dto.JwtRequest;
import com.example.bankcards.dto.JwtResponse;
import com.example.bankcards.dto.SignupRequest;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.service.interfaces.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthorizationController {
  private final AuthService userService;

  public AuthorizationController(AuthService userService) {
    this.userService = userService;
  }

  @PostMapping("/user/login")
  public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
    JwtResponse response = userService.login(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/admin/register")
  public ResponseEntity<?> register(@RequestBody SignupRequest request) {
    userService.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/setup/first-admin")
  public ResponseEntity<?> firstAdmin() {
    String email = "admin";
    String password = "admin";
    userService.signup(new SignupRequest(email, password, Role.ADMIN));
    return ResponseEntity.ok().build();
  }
}
