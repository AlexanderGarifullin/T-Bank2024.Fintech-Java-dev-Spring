package com.fin.spr.exceptions;

public class InvalidTwoFactorCodeException extends RuntimeException {
    public InvalidTwoFactorCodeException() {
        super("two-factor.code_invalid");
    }
}
