package com.example.bankcards.service.impl;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.AlreadyExistsException;
import com.example.bankcards.exception.InvalidCredentialsException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.interfaces.UserRepositoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserRepositoryServiceImpl implements UserRepositoryService {
  private final UserRepository userRepository;

  public UserRepositoryServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public UserEntity get(UUID userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("There is no user with such id!"));
  }

  @Override
  public UserEntity get(String email) {
    return userRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
  }

  @Override
  @Transactional(readOnly = true)
  public void existsById(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new NotFoundException("There is no user with such id!");
    }
  }

  @Override
  @Transactional
  public UserEntity save(UserEntity user) {
    return userRepository.save(user);
  }

  @Override
  public void validateNotExistsByEmail(String email) {
    if (userRepository.existsByEmail(email)) {
      throw new AlreadyExistsException("User with such email already exists!");
    }
  }


}
