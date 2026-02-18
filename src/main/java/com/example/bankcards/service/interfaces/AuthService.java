package com.example.bankcards.service.interfaces;

import com.example.bankcards.dto.JwtRequest;
import com.example.bankcards.dto.JwtResponse;
import com.example.bankcards.dto.SignupRequest;

public interface AuthService {
  void signup(SignupRequest request);
  JwtResponse login(JwtRequest request);
}
