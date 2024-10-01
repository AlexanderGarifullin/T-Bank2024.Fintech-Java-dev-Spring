package com.fin.spr.interfaces;

import com.fin.spr.models.Location;

import java.util.List;
import java.util.Optional;

/**
 * The {@code ILocationService} interface defines the contract for services
 * managing {@link Location} entities. It provides methods to perform
 * operations such as retrieving, creating, updating, and deleting locations.
 */
public interface ILocationService {

    /**
     * Retrieves a list of all locations.
     *
     * @return a {@link List} containing all {@link Location} entities
     */
    List<Location> getAllLocations();

    /**
     * Retrieves a location by its unique slug.
     *
     * @param slug the unique slug of the location to retrieve
     * @return an {@link Optional} containing the found {@link Location}, or empty if not found
     */
    Optional<Location> getLocationBySlug(String slug);

    /**
     * Creates a new location.
     *
     * @param location the {@link Location} entity to be created
     */
    void createLocation(Location location);

    /**
     * Updates an existing location identified by its unique slug.
     *
     * @param slug     the unique slug of the location to update
     * @param location the updated {@link Location} entity
     * @return true if the update was successful, false if the location was not found
     */
    boolean updateLocation(String slug, Location location);

    /**
     * Deletes a location identified by its unique slug.
     *
     * @param slug the unique slug of the location to be deleted
     * @return true if the deletion was successful, false if the location was not found
     */
    boolean deleteLocation(String slug);
}
