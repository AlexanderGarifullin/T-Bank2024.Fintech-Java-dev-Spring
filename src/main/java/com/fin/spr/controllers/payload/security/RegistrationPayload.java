package com.fin.spr.controllers.payload.security;

public record RegistrationPayload(
        String name,
        String login,
        String password
) {
}
