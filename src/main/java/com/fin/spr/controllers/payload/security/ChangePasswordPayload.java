package com.fin.spr.controllers.payload.security;

public record ChangePasswordPayload (
        String newPassword,
        String twoFactorCode
){
}
