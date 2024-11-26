package com.fin.spr.services.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.short}")
    private Duration shortTerm;

    @Value("${jwt.long}")
    private Duration longTerm;

    @Value("${jwt.secret}")
    private String jwtSigningKey;


    public String generateToken(UserDetails userDetails, boolean rememberMe) {
        return generateToken(Map.of(), userDetails, rememberMe);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, boolean rememberMe) {
        var tokenDuration = rememberMe ? longTerm : shortTerm;
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenDuration.toMillis()))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
