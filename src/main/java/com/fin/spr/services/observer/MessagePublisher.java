package com.fin.spr.services.observer;

import com.fin.spr.interfaces.service.observer.Observer;
import com.fin.spr.interfaces.service.observer.Subject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessagePublisher implements Subject {
    private List<Observer> observers = new ArrayList<>();

    @Override
    public void attach(Observer o) {
        observers.add(o);
    }

    @Override
    public void detach(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyUpdate(String message) {
        for (Observer o : observers) {
            o.update(message);
        }
    }
}
