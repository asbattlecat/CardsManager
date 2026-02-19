package com.example.bankcards.service.impl;

import com.example.bankcards.dto.JwtRequest;
import com.example.bankcards.dto.JwtResponse;
import com.example.bankcards.dto.SignupRequest;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.InvalidCredentialsException;
import com.example.bankcards.security.interfaces.JwtProvider;
import com.example.bankcards.service.interfaces.AuthService;
import com.example.bankcards.service.interfaces.UserRepositoryService;
import io.jsonwebtoken.Claims;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
  private final UserRepositoryService userRepositoryService;
  private final JwtProvider jwtProvider;
  private final PasswordEncoder passwordEncoder;

  public AuthServiceImpl(UserRepositoryService userRepositoryService, JwtProvider jwtProvider,
      PasswordEncoder passwordEncoder) {
    this.userRepositoryService = userRepositoryService;
    this.jwtProvider = jwtProvider;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public JwtResponse login(JwtRequest request) {
    UserEntity user = userRepositoryService.getByEmail(request.email());

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new InvalidCredentialsException("Invalid credentials");
    }

    String accessToken = jwtProvider.generateAccessToken(user);
    String refreshToken = jwtProvider.generateRefreshToken(user);

    return new JwtResponse(accessToken, refreshToken);
  }

  @Override
  public void signup(SignupRequest request) {
    // проверка на существование пользователя
    userRepositoryService.validateNotExistsByEmail(request.email());

    String hash = passwordEncoder.encode(request.password());

    UserEntity user = new UserEntity(request.email(), hash, request.role());

    userRepositoryService.save(user);
  }

  /**
   * Метод для создания новой пары access token и refresh token, если refreshToken валиден
   * @param refreshToken токен обновления
   * @return новую пару refresh token и access token
   */
  @Override
  public JwtResponse refresh(@NotNull String refreshToken) {
    if (!jwtProvider.validateRefreshToken(refreshToken)) {
      throw new InvalidCredentialsException("Invalid refresh token");
    }

    Claims claims = jwtProvider.getClaims(refreshToken);
    String email = jwtProvider.claimsToEmail(claims);

    UserEntity user = userRepositoryService.getByEmail(email);

    String newAccessToken = jwtProvider.generateAccessToken(user);
    String newRefreshToken = jwtProvider.generateRefreshToken(user);

    return new JwtResponse(newAccessToken, newRefreshToken);
  }
}
