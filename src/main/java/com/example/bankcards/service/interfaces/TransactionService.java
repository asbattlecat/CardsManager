package com.example.bankcards.service.interfaces;

import javax.management.relation.InvalidRelationIdException;
import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionService {
  void transfer(UUID userId, UUID from, UUID to, BigDecimal amount);
}
