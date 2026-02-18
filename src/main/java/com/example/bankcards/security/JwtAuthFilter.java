package com.example.bankcards.security;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.security.impl.JwtAuthEntryPoint;
import com.example.bankcards.security.interfaces.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Валидация JWT токена и установка пользователя в SecurityContextHolder
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtProvider jwtProvider;
  private final JwtAuthEntryPoint authenticationEntryPoint;
  private final UserDetailsService userDetailsService;

  public JwtAuthFilter(JwtProvider jwtProvider, JwtAuthEntryPoint authenticationEntryPoint,
                       UserDetailsService userDetailsService) {
    this.jwtProvider = jwtProvider;
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
    // достаем токен с проверкой
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    String accessToken = authHeader.substring("Bearer ".length());


    try {
      if (!jwtProvider.validateAccessToken(accessToken)) {
        throw new RuntimeException("Invalid access token");
      }

      Claims claims = jwtProvider.getClaims(accessToken);
      UUID userId = jwtProvider.claimsToUUID(claims);

      UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(userId.toString());

      UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

      // передаем информацию (в данном случае в SecurityConfiguration), что валидация прошла
      // успешно
      SecurityContextHolder.getContext().setAuthentication(token);

      filterChain.doFilter(request, response);
    } catch (RuntimeException e) {
      authenticationEntryPoint.commence(request, response, (AuthenticationException) e);
    }
  }
}
