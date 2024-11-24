package com.fin.spr.services.observer;

import com.fin.spr.interfaces.service.observer.Observer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoggingSubscriber implements Observer {
    @Override
    public void update(String message) {
        log.info(message);
    }
}