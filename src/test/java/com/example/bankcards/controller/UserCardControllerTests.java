package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.security.impl.JwtAuthEntryPoint;
import com.example.bankcards.security.interfaces.JwtProvider;
import com.example.bankcards.service.interfaces.CardService;
import com.example.bankcards.service.interfaces.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserCardController.class)
@Import({UserCardControllerTest.TestSecurityConfig.class, UserCardControllerTest.TestSecurityFilterChainConfig.class})
class UserCardControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CardService cardService;

  @MockBean
  private TransactionService transactionService;

  @MockBean
  private JwtProvider jwtProvider;

  @MockBean
  private JwtAuthEntryPoint jwtAuthEntryPoint;

  @Autowired
  private ObjectMapper objectMapper;

  private static final String USER_EMAIL = "test@example.com";

  // Тестовая конфигурация для UserDetailsService
  @org.springframework.boot.test.context.TestConfiguration
  static class TestSecurityConfig {
    @Bean
    public UserDetailsService userDetailsService() {
      return username -> {
        UserEntity user = new UserEntity(username, "password", Role.USER);
        user.setId(UUID.randomUUID());
        return user;
      };
    }
  }

  // ОТКЛЮЧАЕМ CSRF ДЛЯ ТЕСТОВ
  @org.springframework.boot.test.context.TestConfiguration
  static class TestSecurityFilterChainConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http
              .csrf(csrf -> csrf.disable())
              .authorizeHttpRequests(auth -> auth
                      .anyRequest().authenticated()
              );
      return http.build();
    }
  }

  @Test
  @WithUserDetails(USER_EMAIL)
  void allUsersCards_ShouldReturnListOfIds() throws Exception {
    List<UUID> expectedIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    when(cardService.getUserCardsIds(any(UUID.class))).thenReturn(expectedIds);

    mockMvc.perform(get("/api/user/cards/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0]").value(expectedIds.get(0).toString()))
            .andExpect(jsonPath("$[1]").value(expectedIds.get(1).toString()));

    verify(cardService, times(1)).getUserCardsIds(any(UUID.class));
  }

  @Test
  @WithUserDetails(USER_EMAIL)
  void getUserCardPagination_WithoutStatus_ShouldCallFindUserCards() throws Exception {
    Page<CardResponse> page = new PageImpl<>(List.of(new CardResponse(null, null, null, null, null)));
    PageRequest expectedPageable = PageRequest.of(0, 10, Sort.by("expirationDate").ascending());
    when(cardService.findUserCards(any(UUID.class), any(Pageable.class))).thenReturn(page);

    mockMvc.perform(get("/api/user/cards"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());

    verify(cardService, times(1)).findUserCards(any(UUID.class), eq(expectedPageable));
    verify(cardService, never()).findUserCardsByStatus(any(), any(), any());
  }

  @Test
  @WithUserDetails(USER_EMAIL)
  void getUserCardPagination_WithStatus_ShouldCallFindUserCardsByStatus() throws Exception {
    CardStatus status = CardStatus.ACTIVE;
    Page<CardResponse> page = new PageImpl<>(List.of(new CardResponse(null, null, null, null, null)));
    PageRequest expectedPageable = PageRequest.of(0, 10, Sort.by("expirationDate").ascending());
    when(cardService.findUserCardsByStatus(any(UUID.class), eq(status), any(Pageable.class))).thenReturn(page);

    mockMvc.perform(get("/api/user/cards")
                    .param("status", status.name()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());

    verify(cardService, times(1)).findUserCardsByStatus(any(UUID.class), eq(status), eq(expectedPageable));
    verify(cardService, never()).findUserCards(any(), any());
  }

  @Test
  @WithUserDetails(USER_EMAIL)
  void blockRequest_ShouldReturnOk() throws Exception {
    UUID cardId = UUID.randomUUID();

    mockMvc.perform(post("/api/user/cards/block-request/{cardId}", cardId))
            .andExpect(status().isOk());

    verify(cardService, times(1)).blockRequest(eq(cardId), any(UUID.class));
  }

  @Test
  @WithUserDetails(USER_EMAIL)
  void cardBalance_ShouldReturnBalance() throws Exception {
    UUID cardId = UUID.randomUUID();
    BigDecimal balance = BigDecimal.valueOf(150.75);
    when(cardService.getBalance(eq(cardId), any(UUID.class))).thenReturn(balance);

    mockMvc.perform(get("/api/user/cards/{cardId}", cardId))
            .andExpect(status().isOk())
            .andExpect(content().string(balance.toString()));

    verify(cardService, times(1)).getBalance(eq(cardId), any(UUID.class));
  }

  @Test
  @WithUserDetails(USER_EMAIL)
  void transfer_ShouldReturnOk() throws Exception {
    TransferRequest request = new TransferRequest(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN);

    mockMvc.perform(patch("/api/user/cards/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

    verify(transactionService, times(1))
            .transfer(any(UUID.class), eq(request.from()), eq(request.to()), eq(request.amount()));
  }

  @Test
  @WithUserDetails(USER_EMAIL)
  void transfer_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
    TransferRequest request = new TransferRequest(UUID.randomUUID(), UUID.randomUUID(), null);

    mockMvc.perform(patch("/api/user/cards/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

    verify(transactionService, never())
            .transfer(any(), any(), any(), any());
  }
}