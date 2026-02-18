package com.example.bankcards.security;

import com.example.bankcards.security.model.JwtAuthentication;
import com.example.bankcards.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Валидация JWT токена и установка пользователя в SecurityContextHolder
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtProvider jwtProvider;
  private final JwtAuthEntryPoint authenticationEntryPoint;

  public JwtAuthFilter(JwtProvider jwtProvider, JwtAuthEntryPoint authenticationEntryPoint) {
    this.jwtProvider = jwtProvider;
    this.authenticationEntryPoint = authenticationEntryPoint;
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String accessToken = authHeader.substring("Bearer ".length());

    try {
      if (!jwtProvider.validateAccessToken(accessToken)) {
        authenticationEntryPoint.commence(request, response,
                (AuthenticationException) new RuntimeException("Invalid access token"));
        return;
      }
      Claims claims = jwtProvider.getClaims(accessToken);
      JwtAuthentication jwtAuthentication = JwtUtil.createJwtAuthentication(claims);

      // передаем информацию (в данном случае в SecurityConfiguration), что валидация прошла
      // успешно, и privateFilterChain доволен
      SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);

      filterChain.doFilter(request, response);
    } catch (RuntimeException e) {
      authenticationEntryPoint.commence(request, response, (AuthenticationException) e);
    }
  }
}
