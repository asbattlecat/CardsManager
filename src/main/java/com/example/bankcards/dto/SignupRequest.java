package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;

public record SignupRequest(String email, String password, Role role) {
}
