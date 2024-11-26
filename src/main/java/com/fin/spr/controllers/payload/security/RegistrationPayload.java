package com.fin.spr.controllers.payload.security;

import lombok.Builder;

@Builder
public record RegistrationPayload(
        String name,
        String login,
        String password
) {
}
