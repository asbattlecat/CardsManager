package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlockRequestEntity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequestEntity, UUID> {

  @Query(value = """
    SELECT cbre
    FROM CardBlockRequestEntity AS cbre
  """)
  List<CardBlockRequestEntity> getAllInList();

  boolean existsByCardId(UUID cardId);
}
