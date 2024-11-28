package com.fin.spr.exceptions;

public class EventNotFoundException extends EntityNotFoundException {

    public EventNotFoundException(Long id) {
        super("event.not_found", id);
    }
}
