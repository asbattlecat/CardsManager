package com.example.bankcards.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record TokenRefreshRequest(
        @NotNull
        @NotEmpty
        String refreshToken
) {}
