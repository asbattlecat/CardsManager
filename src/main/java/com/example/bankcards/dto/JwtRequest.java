package com.example.bankcards.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record JwtRequest(
        @NotNull @NotEmpty String email,
        @NotNull @NotEmpty String password
) {}
