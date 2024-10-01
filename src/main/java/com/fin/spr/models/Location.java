package com.fin.spr.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The {@code Location} class represents a location entity with a slug and a name.
 * It is used to identify and describe different locations within the application.
 */
@Data
@AllArgsConstructor
public class Location {
    /**
     * The unique slug for the location, used for URL-friendly identification.
     */
    private String slug;

    /**
     * The name of the location.
     */
    private String name;
}
