package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlockRequestEntity;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface CardBlockRequestRepository extends CrudRepository<CardBlockRequestEntity, UUID> {}
