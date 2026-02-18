package com.example.bankcards.controller;

import com.example.bankcards.service.interfaces.CardService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminCardController {
  private final CardService cardService;

  public AdminCardController(CardService cardService) {
    this.cardService = cardService;
  }


}
