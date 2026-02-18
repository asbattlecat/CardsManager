package com.example.bankcards.security.impl;

import com.example.bankcards.service.interfaces.UserRepositoryService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepositoryService userRepositoryService;

  public UserDetailsServiceImpl(UserRepositoryService userRepositoryService) {
    this.userRepositoryService = userRepositoryService;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepositoryService.getByEmail(email);
  }
}
