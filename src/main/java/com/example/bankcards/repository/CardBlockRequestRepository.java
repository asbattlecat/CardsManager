package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlockRequestEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CardBlockRequestRepository extends CrudRepository<CardBlockRequestEntity, UUID> {
}
