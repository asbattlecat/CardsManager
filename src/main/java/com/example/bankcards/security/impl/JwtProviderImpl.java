package com.example.bankcards.security.impl;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.security.interfaces.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtProviderImpl implements JwtProvider {
  // А ЭТО НЕ БЕЗОПАСНО, Я ЗНАЮ! но это пет проект :))
  private final String SECRET;
  private final SecretKey key;

  public JwtProviderImpl() {
    SECRET = "abc_abc_abc_abc_abc_abc_abc_abc_";
    key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
  }

  public String generateAccessToken(UserEntity user) {
    Instant now = Instant.now();
    Date issuedAt = Date.from(now);
    Date expireAt = Date.from(now.plus(30, ChronoUnit.MINUTES));

    return Jwts.builder()
        .subject(user.getEmail())
        .claim("roles", user.getRole().getAuthority())
        .claim("type", "access")
        .issuedAt(issuedAt)
        .expiration(expireAt)
        .signWith(key, Jwts.SIG.HS256)
        .compact();
  }

  public String generateRefreshToken(UserEntity user) {
    Instant now = Instant.now();
    Date issuedAt = Date.from(now);
    Date expireAt = Date.from(now.plus(1, ChronoUnit.DAYS));

    return Jwts.builder()
        .subject(user.getEmail())
        .claim("type", "refresh")
        .issuedAt(issuedAt)
        .expiration(expireAt)
        .signWith(key, Jwts.SIG.HS256)
        .compact();
  }

  public boolean validateAccessToken(String accessToken) {
    try {
      Claims claims = getClaims(accessToken);
      boolean isAccess = "access".equals(claims.get("type", String.class));
      return isTokenValid(claims) && isAccess;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean validateRefreshToken(String refreshToken) {
    try {
      Claims claims = getClaims(refreshToken);
      boolean isRefresh = "refresh".equals(claims.get("type", String.class));
      return isTokenValid(claims) && isRefresh;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Метод проверяет, что claims не были изменены, и возвращает сами claims
   * @param token access или refresh token
   * @return Claims из токена
   */
  public Claims getClaims(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }

  public String claimsToEmail(Claims claims) {
    return claims.getSubject();
  }

  /**
   * Метод для проверки валидности переданного токена
   * @param claims claims из токена
   * @return <code>true</code> - срок токена не истек, <code>false</code> - срок токена истек
   */
  private boolean isTokenValid(Claims claims) {
    Date expiration = claims.getExpiration();
    return expiration != null && expiration.after(new Date());
  }
}
