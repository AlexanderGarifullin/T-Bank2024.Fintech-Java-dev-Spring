package com.fin.spr.repository.security;

import com.fin.spr.models.security.Token;
import com.fin.spr.models.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    List<Token> findAllByUserAndRevoked(User user, boolean revoked);
}
