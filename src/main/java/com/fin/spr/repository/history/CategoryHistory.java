package com.fin.spr.repository.history;

import com.fin.spr.models.Category;
import com.fin.spr.models.memento.CategoryMemento;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CategoryHistory {

    private final List<CategoryMemento> mementos = new ArrayList<>();

    public void add(CategoryMemento memento) {
        mementos.add(memento);
    }

    public CategoryMemento get(int index) {
        return mementos.get(index);
    }

    public CategoryMemento getLast() {
        return mementos.getLast();
    }
}
