package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlockRequestEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CardBlockRequestRepository extends CrudRepository<CardBlockRequestEntity, UUID> {

  @Query(value = """
    SELECT cbre
    FROM CardBlockRequestEntity AS cbre
  """)
  List<CardBlockRequestEntity> getAllInList();

  Optional<CardBlockRequestEntity> findByCardId(UUID cardId);
}
