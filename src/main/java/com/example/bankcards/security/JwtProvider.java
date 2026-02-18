package com.example.bankcards.security;

import com.example.bankcards.entity.UserEntity;
import io.jsonwebtoken.Claims;
import java.util.UUID;

public interface JwtProvider {
  String generateAccessToken(UserEntity user);
  String generateRefreshToken(UserEntity user);
  boolean validateAccessToken(String accessToken);
  boolean validateRefreshToken(String refreshToken);
  Claims getClaims(String token);
  UUID claimsToUUID(Claims claims);
}
