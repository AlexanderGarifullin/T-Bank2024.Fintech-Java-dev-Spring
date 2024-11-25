package com.fin.spr.auth;

public record RegistrationRequest (
        String name,
        String login,
        String password
) {
}
