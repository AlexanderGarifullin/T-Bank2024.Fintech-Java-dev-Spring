package com.fin.spr.interfaces;

import com.fin.spr.models.Location;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * The {@code ILocationController} interface defines the contract for managing location-related operations
 * in the application. It provides methods for performing CRUD operations on {@link Location} entities.
 */
public interface ILocationController {

    /**
     * Retrieves a list of all locations.
     *
     * @return a {@link ResponseEntity} containing a list of {@link Location} entities
     */
    ResponseEntity<List<Location>> getAllLocations();

    /**
     * Retrieves a location by its unique slug.
     *
     * @param slug the unique slug of the location to retrieve
     * @return a {@link ResponseEntity} containing the found {@link Location}, or an error response if not found
     */
    ResponseEntity<Location> getLocationBySlug(String slug);

    /**
     * Creates a new location.
     *
     * @param location the {@link Location} entity to be created
     * @return a {@link ResponseEntity} containing the created {@link Location}
     */
    ResponseEntity<Location> createLocation(Location location);

    /**
     * Updates an existing location by its unique slug.
     *
     * @param slug     the unique slug of the location to update
     * @param location the updated {@link Location} entity
     * @return a {@link ResponseEntity} containing the updated {@link Location}, or an error response if not found
     */
    ResponseEntity<Location> updateLocation(String slug, Location location);

    /**
     * Deletes a location by its unique slug.
     *
     * @param slug the unique slug of the location to be deleted
     * @return a {@link ResponseEntity} indicating the outcome of the deletion operation
     */
    ResponseEntity<Void> deleteLocation(String slug);
}
