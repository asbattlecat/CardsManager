package com.example.bankcards.dto;

import java.util.UUID;

public record CreateCardRequest(String cardNumber, UUID userId) {}
