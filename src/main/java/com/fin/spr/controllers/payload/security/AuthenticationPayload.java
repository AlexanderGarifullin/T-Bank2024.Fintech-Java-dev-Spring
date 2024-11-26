package com.fin.spr.controllers.payload.security;

import lombok.Builder;

@Builder
public record AuthenticationPayload (
        String login,
        String password,
        boolean rememberMe
) {
}
