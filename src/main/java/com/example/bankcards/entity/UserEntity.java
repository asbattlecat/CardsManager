package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity implements UserDetails {
  @Id
  private UUID id;
  @Column(nullable = false, unique = true)
  private String email;
  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "role_name")
  private Role role;

  public UserEntity(String email, String password, Role role) {
    id = UUID.randomUUID();
    this.email = email;
    this.password = password;
    this.role = role;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(role);
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }
}
