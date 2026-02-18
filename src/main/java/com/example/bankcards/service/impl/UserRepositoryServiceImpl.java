package com.example.bankcards.service.impl;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.AlreadyExistsException;
import com.example.bankcards.exception.InvalidCredentialsException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.interfaces.UserRepositoryService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRepositoryServiceImpl implements UserRepositoryService {
  private final UserRepository userRepository;

  public UserRepositoryServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Получение UserEntity из репозитория, если в БД он есть. Кидает <code>NotFoundException</code>,
   * если пользователя с таким ID нет
   * @param userId ID искомого пользователя (UUID)
   * @return объект UserEntity, у которого указанный userId
   */
  @Override
  @Transactional(readOnly = true)
  public UserEntity get(UUID userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("There is no user with such id!"));
  }

  /**
   * Получение UserEntity из репозитория, если в БД он есть. Кидает <code>NotFoundException</code>,
   * если пользователя с таким ID нет
   * @param userId строка, содержащая ID пользователя
   * @return объект UserEntity, у которого указанный <code>userId</code>
   */
  @Override
  public UserEntity get(String userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("There is no user with such id!"));
  }


  /**
   * Получение UserEntity из репозитория по email, если в БД он есть. Кидает <code>NotFoundException</code>,
   * если пользователя с таким email нет
   * @param email строка, содержащая email пользователя
   * @return объект UserEntity, у которого указанный <code>email</code>
   */
  @Override
  public UserEntity getByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(
        () -> new InvalidCredentialsException("Invalid credentials"));
  }

  /**
   * Проверяет, существует ли пользователь в БД с указанным ID
   * @param userId ID искомого пользователя
   */
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

  /**
   * Проверяет, что пользователя с указанным email нет
   * @param email искомый email
   */
  @Override
  public void validateNotExistsByEmail(String email) {
    if (userRepository.existsByEmail(email)) {
      throw new AlreadyExistsException("User with such email already exists!");
    }
  }
}
