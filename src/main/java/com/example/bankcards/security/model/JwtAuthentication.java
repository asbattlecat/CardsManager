package com.example.bankcards.security.model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class JwtAuthentication implements Authentication {
  private final UUID id;
  private final Collection<? extends GrantedAuthority> authorities;
  private boolean isAuthenticated;

  public JwtAuthentication(UUID id, Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.authorities = authorities;
    isAuthenticated = true;
  }

  public JwtAuthentication() {
    this(null, Collections.emptyList());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getDetails() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return id;
  }

  @Override
  public boolean isAuthenticated() {
    return isAuthenticated && id != null;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    this.isAuthenticated = isAuthenticated;
  }

  @Override
  public String getName() {
    return id != null ? id.toString() : null;
  }
}
