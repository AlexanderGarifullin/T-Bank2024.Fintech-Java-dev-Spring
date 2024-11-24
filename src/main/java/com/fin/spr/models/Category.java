package com.fin.spr.models;

import com.fin.spr.models.memento.CategoryMemento;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The {@code Category} class represents a category entity with an identifier,
 * a slug, and a name. It is used to categorize entities within the application.
 */
@Data
@AllArgsConstructor
public class Category {
    /**
     * The unique identifier for the category.
     */
    private int id;

    /**
     * The slug of the category, used for URL-friendly identification.
     */
    private String slug;

    /**
     * The name of the category.
     */
    private String name;

    public CategoryMemento save(CrudAction action) {
        return new CategoryMemento(id, slug, name, action, System.currentTimeMillis());
    }

    public void restore(CategoryMemento memento) {
        this.id = memento.getId();
        this.slug = memento.getSlug();
        this.name = memento.getName();
    }
}
