package com.fin.spr.services.security;

import com.fin.spr.exceptions.TokenNotFoundException;
import com.fin.spr.repository.security.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService  {

    private final TokenRepository tokenRepository;

    public boolean isTokenRevoked(String token) {
        return tokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException(token))
                .isRevoked();
    }
}

