package com.fin.spr.exceptions;

import lombok.Getter;

@Getter
public class TokenRevokedException extends RuntimeException {

    private final String token;

    public TokenRevokedException(String token) {
        super("token.is_revoked");
        this.token = token;
    }
}
