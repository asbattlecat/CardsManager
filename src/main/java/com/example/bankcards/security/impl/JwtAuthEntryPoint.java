package com.example.bankcards.security.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper objectMapper;

  public JwtAuthEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  // просто определяет то, как сервер отвечает на запросы пользователя, который не аутентифицирован
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
                       AuthenticationException authException) throws IOException, ServletException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value()); // не авторизован
    response.setContentType(MediaType.APPLICATION_JSON_VALUE); // ответ будет json
    response.setCharacterEncoding("UTF-8");

    Map<String, Object> bodyResponse = new HashMap<>();

    bodyResponse.put("status", HttpStatus.UNAUTHORIZED);
    bodyResponse.put("error", "Unauthorized");
    bodyResponse.put("message", authException.getMessage());

    objectMapper.writeValue(response.getOutputStream(), bodyResponse);
  }
}
