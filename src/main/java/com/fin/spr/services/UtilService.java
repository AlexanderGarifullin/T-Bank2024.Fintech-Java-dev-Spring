package com.fin.spr.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilService {

    public UUID generateSomeId(){
        return UUID.randomUUID();
    }
}
