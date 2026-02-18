package com.example.bankcards.util;

import com.example.bankcards.entity.Role;
import com.example.bankcards.security.model.JwtAuthentication;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JwtUtil {
  public static JwtAuthentication createJwtAuthentication(Claims claims) {
    UUID id = UUID.fromString(claims.getSubject());

    List<?> rawList = claims.get("roles", List.class);
    List<String> roleStrings = rawList.stream()
            .map(Object::toString)
            .toList();

    Collection<GrantedAuthority> authorities = roleStrings.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    return new JwtAuthentication(id, authorities);
  }
}
