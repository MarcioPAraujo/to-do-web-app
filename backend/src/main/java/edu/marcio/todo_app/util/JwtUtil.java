package edu.marcio.todo_app.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

@Component
@Getter
public class JwtUtil {
  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private long expiration;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(String userName) {
    return Jwts.builder()
        .subject(userName)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey())
        .compact();
  }

  public Claims claimJwtBody(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public String extractuserName(String token) {
    return claimJwtBody(token).getSubject();
  }

  public boolean isTokenExpired(String token) {
    return claimJwtBody(token).getExpiration().before(new Date());
  }

  public boolean isTokenValid(UserDetails userDetails, String userName, String token) {
    return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }
}
