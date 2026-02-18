package com.example.bankcards.security;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.security.impl.JwtAuthEntryPoint;
import com.example.bankcards.security.interfaces.JwtProvider;
import com.example.bankcards.service.interfaces.UserRepositoryService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
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
  private final UserDetailsService userDetailsService;
  private final UserRepositoryService userRepositoryService;

  public JwtAuthFilter(JwtProvider jwtProvider, JwtAuthEntryPoint authenticationEntryPoint,
                       UserDetailsService userDetailsService, UserRepositoryService userRepositoryService) {
    this.jwtProvider = jwtProvider;
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.userDetailsService = userDetailsService;
    this.userRepositoryService = userRepositoryService;
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
        throw new RuntimeException();
      }

      Claims claims = jwtProvider.getClaims(accessToken);
      String email = jwtProvider.claimsToEmail(claims);

      UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(email);

      UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

      // передаем информацию (в данном случае в SecurityConfiguration), что валидация прошла
      // успешно
      SecurityContextHolder.getContext().setAuthentication(token);

      filterChain.doFilter(request, response);
    } catch (RuntimeException e) {
      authenticationEntryPoint.commence(request, response, new BadCredentialsException("Invalid access token"));
    }
  }
}
