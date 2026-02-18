package com.example.bankcards.service.interfaces;

import java.math.BigDecimal;
import java.util.UUID;
import javax.management.relation.InvalidRelationIdException;

public interface TransactionService {
  void transfer(UUID userId, UUID from, UUID to, BigDecimal amount);
}
