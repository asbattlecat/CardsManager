package com.example.bankcards.repository;

import com.example.bankcards.entity.CardEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CardRepository extends CrudRepository<CardEntity, UUID> {
}
