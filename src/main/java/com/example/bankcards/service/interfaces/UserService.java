package com.example.bankcards.service.interfaces;

import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.RegistrationRequest;

public interface UserService {
  void register(RegistrationRequest request);
  AuthResponse login(LoginRequest request);
//  void changeStatus();
//  void changeRole();
//  void findUser();
//  void checkAccess();
}
