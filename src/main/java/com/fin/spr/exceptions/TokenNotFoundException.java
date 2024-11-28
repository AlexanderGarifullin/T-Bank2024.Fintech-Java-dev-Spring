package com.fin.spr.exceptions;


import lombok.Getter;

@Getter
public class TokenNotFoundException extends RuntimeException  {
    private final String token;

    public TokenNotFoundException(String token) {
        super("token.not_found");
        this.token = token;
    }
}