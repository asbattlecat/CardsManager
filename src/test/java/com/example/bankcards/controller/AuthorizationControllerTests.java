package com.example.bankcards.controller;


import com.example.bankcards.dto.JwtRequest;
import com.example.bankcards.dto.JwtResponse;
import com.example.bankcards.dto.SignupRequest;
import com.example.bankcards.dto.TokenRefreshRequest;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.AlreadyExistsException;
import com.example.bankcards.exception.ControllerExceptionsHandler;
import com.example.bankcards.exception.InvalidCredentialsException;
import com.example.bankcards.service.interfaces.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Тесты писались в торопях, прошу прощения за беспорядок :)
@ExtendWith(MockitoExtension.class)
public class AuthorizationControllerTests {
  private MockMvc mockMvc;

  @Mock
  private AuthService authService;

  @InjectMocks
  private AuthorizationController authorizationController;

  private ObjectMapper objectMapper;
  private JwtRequest validLoginRequest;
  private SignupRequest validSignupRequest;
  private JwtResponse jwtResponse;

  private UUID userId;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(authorizationController)
            .setControllerAdvice(new ControllerExceptionsHandler())
            .build();

    objectMapper = new ObjectMapper();

    validLoginRequest = new JwtRequest("email", "password");
    validSignupRequest = new SignupRequest("email", "password", Role.USER);
    jwtResponse = new JwtResponse("access-token", "refresh-token");
  }

  @Test
  void login_Success() throws Exception {
    when(authService.login(any(JwtRequest.class))).thenReturn(jwtResponse);

    mockMvc.perform(post("/api/user/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validLoginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("access-token"))
            .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

    verify(authService, times(1)).login(any(JwtRequest.class));
  }

  @Test
  void login_InvalidCredentials_Unauthorized() throws Exception {
    when(authService.login(any(JwtRequest.class)))
            .thenThrow(new InvalidCredentialsException("Invalid credentials"));

    mockMvc.perform(post("/api/user/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validLoginRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(content().string("Invalid credentials"));
  }

  @Test
  void login_UserNotFound_Unauthorized() throws Exception {
    when(authService.login(any(JwtRequest.class)))
            .thenThrow(new InvalidCredentialsException("Invalid credentials"));

    mockMvc.perform(post("/api/user/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validLoginRequest)))
            .andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @MethodSource("provideInvalidLoginRequests")
  void login_InvalidInput_BadRequest(JwtRequest invalidRequest) throws Exception {
    mockMvc.perform(post("/api/user/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());

    verify(authService, never()).login(any());
  }

  private static Stream<Arguments> provideInvalidLoginRequests() {
    return Stream.of(
            Arguments.of(new JwtRequest(null, "password")),
            Arguments.of(new JwtRequest("", "password")),
            Arguments.of(new JwtRequest("email", null)),
            Arguments.of(new JwtRequest("email", ""))
    );
  }

  @Test
  void register_Success_ReturnsCreated() throws Exception {
    doNothing().when(authService).signup(any(SignupRequest.class));

    mockMvc.perform(post("/api/admin/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validSignupRequest)))
            .andExpect(status().isCreated());

    verify(authService, times(1)).signup(any(SignupRequest.class));
  }

  @Test
  void register_WhenUserAlreadyExists_ReturnsConflict() throws Exception {
    doThrow(new AlreadyExistsException("User with such email already exists!"))
            .when(authService).signup(any(SignupRequest.class));

    mockMvc.perform(post("/api/admin/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validSignupRequest)))
            .andExpect(status().isConflict())
            .andExpect(content().string("User with such email already exists!"));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidSignupRequests")
  void register_WithInvalidInput_ReturnsBadRequest(SignupRequest invalidRequest) throws Exception {
    mockMvc.perform(post("/api/admin/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());

    verify(authService, never()).signup(any());
  }

  private static Stream<Arguments> provideInvalidSignupRequests() {
    return Stream.of(
            Arguments.of(new SignupRequest(null, "password", Role.USER)),
            Arguments.of(new SignupRequest("", "password", Role.USER)),
            Arguments.of(new SignupRequest("email@test.com", null, Role.USER)),
            Arguments.of(new SignupRequest("email@test.com", "", Role.USER)),
            Arguments.of(new SignupRequest("email@test.com", "password", null))
    );
  }

  @Test
  void refreshTokens_Success_Return200() throws Exception {
    TokenRefreshRequest request = new TokenRefreshRequest("refresh_token");
    when(authService.refresh(any(String.class))).thenReturn(jwtResponse);

    mockMvc.perform(post("/api/user/refresh-tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
  }

  @Test
  void refreshToken_Fail_BadRequest() throws Exception {
    TokenRefreshRequest request = new TokenRefreshRequest(null);

    mockMvc.perform(post("/api/user/refresh-tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
  }

}
