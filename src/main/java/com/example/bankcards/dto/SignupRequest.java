package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.Role;

public record SignupRequest(String email, String password, Role role) {}
