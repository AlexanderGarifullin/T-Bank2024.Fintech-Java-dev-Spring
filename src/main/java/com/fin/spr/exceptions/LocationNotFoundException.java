package com.fin.spr.exceptions;

public class LocationNotFoundException  extends EntityNotFoundException{
    public LocationNotFoundException(Long id) {
        super("location.not_found", id);
    }
}
