package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.lang.NonNull;

import java.util.UUID;

public record CreateCardRequest(
        @NotNull
        @Size(min = 16, max = 16, message = "Поле должно содержать ровно 16 символов")
        String cardNumber,
        @NotNull
        UUID userId
) {}
