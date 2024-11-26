package com.fin.spr.controllers.payload.security;

public record AuthenticationPayload (
        String login,
        String password,
        boolean rememberMe
) {
}
