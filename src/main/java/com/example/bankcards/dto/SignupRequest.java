package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SignupRequest(
        @NotNull @NotEmpty String email,
        @NotNull @NotEmpty String password,
        @NotNull Role role) {}
