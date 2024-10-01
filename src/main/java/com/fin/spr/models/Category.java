package com.fin.spr.models;

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
}
