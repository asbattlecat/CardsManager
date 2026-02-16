package com.example.bankcards.service.impl;

import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.RegistrationRequest;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.InvalidCredentialsException;
import com.example.bankcards.exception.AlreadyExistsException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.interfaces.JwtProvider;
import com.example.bankcards.service.interfaces.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final JwtProvider jwtProvider;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, JwtProvider jwtProvider, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.jwtProvider = jwtProvider;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void register(RegistrationRequest request) {
    // проверка на существование пользователя
    if (userRepository.existsByEmail(request.email())) {
      throw new AlreadyExistsException("User with such email already exists!");
    }

    String hash = passwordEncoder.encode(request.password());

    UserEntity user = new UserEntity(request.email(), hash, request.role());

    userRepository.save(user);
  }

  @Override
  public AuthResponse login(LoginRequest request) {
    Optional<UserEntity> userEntityOptional = userRepository.findByEmail(request.email());
    if (userEntityOptional.isEmpty()) {
      throw new InvalidCredentialsException("Invalid credentials");
    }

    UserEntity user = userEntityOptional.get();

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new InvalidCredentialsException("Invalid credentials");
    }

    String accessToken = jwtProvider.generateAccessToken(user);
    String refreshToken = jwtProvider.generateRefreshToken(user);

    return new AuthResponse(accessToken, refreshToken);
  }
}
