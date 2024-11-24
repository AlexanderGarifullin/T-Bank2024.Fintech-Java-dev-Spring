package com.fin.spr.models.memento;

import com.fin.spr.models.CrudAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryMemento {
    private final int id;
    private final String slug;
    private final String name;
    private final CrudAction action;
    private final long timestamp;
}
