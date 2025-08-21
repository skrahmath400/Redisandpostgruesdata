package com.redis.jwttoken;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import com.redis.entity.Overalldata;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

 @Value("${jwt.secret:}")          // Base64 preferred; raw allowed
 private String configuredSecret;

 @Value("${jwt.exp.minutes:60}")
 private long expiryMinutes;

 private SecretKey key;
 private String base64SecretIfGenerated;

 @PostConstruct
 void init() {
     if (configuredSecret == null || configuredSecret.isBlank()) {
         try {
             KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
             SecretKey sk = keyGen.generateKey();
             this.key = sk;
             this.base64SecretIfGenerated = Base64.getEncoder().encodeToString(sk.getEncoded());
             System.out.println("Generated HS256 Base64 secret (dev only): " + base64SecretIfGenerated);
         } catch (NoSuchAlgorithmException e) {
             throw new RuntimeException("Unable to generate HS256 key", e);
         }
     } else {
         try {
             byte[] decoded = Base64.getDecoder().decode(configuredSecret);
             this.key = Keys.hmacShaKeyFor(decoded);
         } catch (IllegalArgumentException notBase64) {
             byte[] raw = configuredSecret.getBytes(StandardCharsets.UTF_8);
             this.key = Keys.hmacShaKeyFor(raw);
         }
     }
 }

 /** Existing method: minimal token (add your claims as needed) */
 public String generateToken(Overalldata emp) {
     Instant now = Instant.now();
     Instant exp = now.plusSeconds(expiryMinutes * 60);

     return Jwts.builder()
             .id(UUID.randomUUID().toString())                     // jti
             .subject(Integer.toString(emp.getEmployeeId()))       // sub
             .issuedAt(Date.from(now))
             .expiration(Date.from(exp))
             .claim("uid", emp.getEmployeeId())
             .signWith(key)                                        // HS256 inferred
             .compact();
 }

 /** NEW: Overload to include extra claims (role, screen_permissions, etc.) */
 public String generateToken(Overalldata emp, Map<String, Object> extraClaims) {
     Instant now = Instant.now();
     Instant exp = now.plusSeconds(expiryMinutes * 60);

     var b = Jwts.builder()
             .id(UUID.randomUUID().toString())
             .subject(Integer.toString(emp.getEmployeeId()))
             .issuedAt(Date.from(now))
             .expiration(Date.from(exp))
             .claim("uid", emp.getEmployeeId());
     if (extraClaims != null && !extraClaims.isEmpty()) {
         b.claims(extraClaims); // jjwt 0.12.x
     }
     return b.signWith(key).compact();
 }

 /** NEW: Parse and return the Claims (this is what your code is missing). */
 public Claims parseClaims(String jwt) {
     return Jwts.parser()
             .verifyWith(key)          // 0.12.x API
             .build()
             .parseSignedClaims(jwt)
             .getPayload();
 }

 /** Handy helper: get exp as epoch seconds (used in your JwtResponse). */
 public long getExpiryEpochSeconds(String jwt) {
     return parseClaims(jwt).getExpiration().toInstant().getEpochSecond();
 }

 /** Optional: quick validity check against expected uid. */
 public boolean isValidFor(String jwt, int expectedUid) {
     try {
         Claims c = parseClaims(jwt);
         return String.valueOf(expectedUid).equals(c.getSubject())
                 && c.getExpiration().toInstant().isAfter(Instant.now());
     } catch (Exception e) {
         return false;
     }
 }

 /** Dev helper if you generated a secret at runtime. */
 public Optional<String> generatedBase64Secret() {
     return Optional.ofNullable(base64SecretIfGenerated);
 }
}
