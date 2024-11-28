package com.fin.spr.controllers.payload.security;

import lombok.Builder;

@Builder
public record ChangePasswordPayload(
        String newPassword,
        String twoFactorCode
) {
}
