package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.CardStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CardResponse(UUID id, String maskedNumber, BigDecimal balance,
    LocalDate expirationDate, CardStatus status) {}
