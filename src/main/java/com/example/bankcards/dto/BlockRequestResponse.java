package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.BlockRequestStatus;

import java.util.UUID;

public record BlockRequestResponse(
        UUID requestId,
        UUID cardId,
        UUID ownerId,
        BlockRequestStatus status
) {
}
