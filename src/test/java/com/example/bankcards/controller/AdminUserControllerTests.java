package com.example.bankcards.controller;

import com.example.bankcards.exception.ControllerExceptionsHandler;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.service.interfaces.UserRepositoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminUserControllerTests {
  private MockMvc mockMvc;

  @Mock
  private UserRepositoryService userRepositoryService;

  @InjectMocks
  private AdminUserController adminUserController;

  private ObjectMapper objectMapper;
  private UUID userId;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(adminUserController)
            .setControllerAdvice(new ControllerExceptionsHandler())
            .build();

    userId = UUID.randomUUID();
    objectMapper = new ObjectMapper();
  }

  @Test
  void deleteUser_Success() throws Exception {
    mockMvc.perform(delete("/api/admin/users/{userId}", userId))
            .andExpect(status().isOk());

    verify(userRepositoryService, times(1)).delete(userId);
  }

  @Test
  void deleteUser_NotFound() throws Exception {
    doThrow(new NotFoundException("There is no user with such id!"))
            .when(userRepositoryService).delete(userId);

    mockMvc.perform(delete("/api/admin/users/{userId}", userId))
            .andExpect(status().isNotFound());
    verify(userRepositoryService, times(1)).delete(userId);
  }
}
