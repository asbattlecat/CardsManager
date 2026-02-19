package com.example.bankcards.controller;

import com.example.bankcards.dto.BlockRequestResponse;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.ControllerExceptionsHandler;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.service.interfaces.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminCardControllerTests {
  private MockMvc mockMvc;

  @Mock
  private CardService cardService;

  @InjectMocks
  private AdminCardController adminCardController;

  private ObjectMapper objectMapper;
  private UUID cardId;
  private UUID userId;
  private UUID requestId;
  private CardResponse cardResponse;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(adminCardController)
            .setControllerAdvice(new ControllerExceptionsHandler())
            .build();

    cardId = UUID.randomUUID();
    userId = UUID.randomUUID();
    requestId = UUID.randomUUID();

    objectMapper = new ObjectMapper();

    cardResponse = new CardResponse(
            cardId,
            "**** **** **** 1234",
            BigDecimal.ZERO,
            LocalDate.now().plusYears(5),
            CardStatus.ACTIVE
    );
  }

  @Test
  void create_Success_ReturnsCardResponse() throws Exception {
    CreateCardRequest request = new CreateCardRequest("1234123412341234", userId);

    when(cardService.create(any(CreateCardRequest.class))).thenReturn(cardResponse);

    mockMvc.perform(post("/api/admin/cards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(cardId.toString()))
            .andExpect(jsonPath("$.maskedNumber").value("**** **** **** 1234"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  void create_WithInvalidCardNumber_ReturnsBadRequest() throws Exception {
    CreateCardRequest invalidRequest = new CreateCardRequest(
            "123",
            userId
    );

    mockMvc.perform(post("/api/admin/cards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
  }

  @Test
  void deleteCard_Success_ReturnsOk() throws Exception {
    mockMvc.perform(delete("/api/admin/cards/{cardId}", cardId))
            .andExpect(status().isOk());

    verify(cardService, times(1)).delete(cardId);
  }

  @Test
  void blockCard_Success_ReturnsOk() throws Exception {
    mockMvc.perform(patch("/api/admin/cards/{cardId}/block", cardId))
            .andExpect(status().isOk());

    verify(cardService, times(1)).block(cardId);
  }

  @Test
  void activateCard_Success_ReturnsOk() throws Exception {
    mockMvc.perform(patch("/api/admin/cards/{cardId}/activate", cardId))
            .andExpect(status().isOk());
    verify(cardService, times(1)).activate(cardId);
  }

  @Test
  void approveBlock_Success_ReturnsOk() throws Exception {
    mockMvc.perform(patch("/api/admin/cards/approve-block/{requestId}", requestId))
            .andExpect(status().isOk());
    verify(cardService, times(1)).approveBlock(requestId);
  }

  @Test
  void approveBlock_NotPending_ReturnsConflict() throws Exception {
    doThrow(new IllegalStateException("Block request is already processed!"))
            .when(cardService).approveBlock(requestId);

    mockMvc.perform(patch("/api/admin/cards/approve-block/{requestId}", requestId))
            .andExpect(status().isConflict());
  }

  @Test
  void rejectBlock_NotPending_ReturnsConflict() throws Exception {
    doThrow(new IllegalStateException("Block request is already processed!"))
            .when(cardService).rejectBlock(requestId);

    mockMvc.perform(patch("/api/admin/cards/reject-block/{requestId}", requestId))
            .andExpect(status().isConflict());
  }

  @Test
  void rejectBlock_Success() throws  Exception {
    mockMvc.perform(patch("/api/admin/cards/reject-block/{requestId}", requestId))
            .andExpect(status().isOk());

    verify(cardService, times(1)).rejectBlock(requestId);
  }

  @Test
  void getAllCards_Success_ReturnsCardIds() throws Exception {
    List<UUID> cardIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    when(cardService.getAllCards()).thenReturn(cardIds);

    mockMvc.perform(get("/api/admin/cards/allCards"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));

    verify(cardService, times(1)).getAllCards();
  }

  @Test
  void getAllBlockRequests_Success_ReturnsRequests() throws Exception {
    BlockRequestResponse request1 = new BlockRequestResponse(
            UUID.randomUUID(),
            cardId,
            userId,
            BlockRequestStatus.PENDING
    );
    BlockRequestResponse request2 = new BlockRequestResponse(
            UUID.randomUUID(),
            cardId,
            userId,
            BlockRequestStatus.APPROVED
    );

    List<BlockRequestResponse> requests = List.of(request1, request2);
    when(cardService.getAllBlockRequests()).thenReturn(requests);

    mockMvc.perform(get("/api/admin/cards/all-block-requests"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].status").value("PENDING"))
            .andExpect(jsonPath("$[1].status").value("APPROVED"));

    verify(cardService, times(1)).getAllBlockRequests();
  }

  @Test
  void deleteCard_WhenCardNotFound_Returns404() throws Exception {
    doThrow(new NotFoundException("There is no card with such id!"))
            .when(cardService).delete(cardId);

    mockMvc.perform(delete("/api/admin/cards/{cardId}", cardId))
            .andExpect(status().isNotFound())
            .andExpect(content().string("There is no card with such id!"));
  }

  @Test
  void blockCard_WhenCardAlreadyBlocked_Returns400() throws Exception {
    doThrow(new IllegalStateException("Only active card can be used for this operation!"))
            .when(cardService).block(cardId);

    mockMvc.perform(patch("/api/admin/cards/{cardId}/block", cardId))
            .andExpect(status().isConflict())
            .andExpect(content().string("Only active card can be used for this operation!"));;
  }

  @Test
  void createCard_WhenNotFound_Returns404() throws Exception {
    CreateCardRequest request = new CreateCardRequest(
            "1234123412341234",
            userId
    );

    when(cardService.create(any(CreateCardRequest.class)))
            .thenThrow(new NotFoundException("There is no user with such id!"));

    mockMvc.perform(post("/api/admin/cards")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(content().string("There is no user with such id!"));
  }

  @Test
  void createCard_WithNullUserId_ReturnsBadRequest() throws Exception {
    CreateCardRequest request = new CreateCardRequest("1234123412341234", null);

    mockMvc.perform(post("/api/admin/cards")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
  }

  @Test
  void createCardWithNullCardNumber_ReturnsBadRequest() throws Exception {
    CreateCardRequest request = new CreateCardRequest(null, userId);

    mockMvc.perform(post("/api/admin/cards")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
  }
}
