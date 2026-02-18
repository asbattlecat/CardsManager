package com.example.bankcards.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
  private final ObjectMapper objectMapper;

  public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  // определяет то, как сервер отвечает на недостаток прав доступа
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
                     AccessDeniedException accessDeniedException) throws IOException, ServletException {
    response.setStatus(HttpStatus.FORBIDDEN.value()); // запрещено
    response.setContentType(MediaType.APPLICATION_JSON_VALUE); // ответ будет json
    response.setCharacterEncoding("UTF-8");

    Map<String, Object> responseBody = new HashMap<>();

    responseBody.put("status", HttpStatus.FORBIDDEN);
    responseBody.put("error", "Forbidden");
    responseBody.put("message", accessDeniedException.getMessage());

    objectMapper.writeValue(response.getOutputStream(), responseBody);
  }
}
