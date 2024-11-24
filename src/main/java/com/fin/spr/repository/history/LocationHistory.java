package com.fin.spr.repository.history;

import com.fin.spr.models.memento.LocationMemento;

import java.util.ArrayList;
import java.util.List;

public class LocationHistory {
    private final List<LocationMemento> mementos = new ArrayList<>();

    public void add(LocationMemento memento) {
        mementos.add(memento);
    }

    public LocationMemento get(int index) {
        return mementos.get(index);
    }

    public LocationMemento getLast() {
        return mementos.getLast();
    }
}
