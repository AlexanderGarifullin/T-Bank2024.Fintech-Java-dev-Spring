package com.fin.spr.exceptions;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
    private final Long id;

    public EntityNotFoundException(String message, Long id) {
        super(message);
        this.id = id;
    }
}
