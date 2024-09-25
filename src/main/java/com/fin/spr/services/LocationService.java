package com.fin.spr.services;

import com.fin.spr.interfaces.ILocationService;
import com.fin.spr.models.Location;
import com.fin.spr.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The {@code LocationService} class provides the implementation of the {@link ILocationService} interface
 * for managing locations. It utilizes an in-memory storage solution to perform CRUD operations
 * on {@link Location} entities.
 */
@Service
public class LocationService implements ILocationService {

    @Autowired
    private InMemoryStorage<Location, String> locationStorage;

    /**
     * Retrieves a list of all locations stored in the system.
     *
     * @return a list of all {@link Location} entities
     */
    @Override
    public List<Location> getAllLocations() {
        return locationStorage.getAll();
    }

    /**
     * Retrieves a location by its unique slug.
     *
     * @param slug the unique slug of the location to retrieve
     * @return an {@link Optional} containing the found {@link Location}, or empty if not found
     */
    @Override
    public Optional<Location> getLocationBySlug(String slug) {
        return locationStorage.getById(slug);
    }

    /**
     * Creates a new location in the storage.
     *
     * @param location the {@link Location} entity to be created
     */
    @Override
    public void createLocation(Location location) {
        locationStorage.create(location.getSlug(), location);
    }

    /**
     * Updates an existing location in the storage.
     *
     * @param slug     the unique slug of the location to update
     * @param location the updated {@link Location} entity
     * @return true if the update was successful, false if the location was not found
     */
    @Override
    public boolean updateLocation(String slug, Location location) {
        return locationStorage.update(slug, location);
    }

    /**
     * Deletes a location from the storage by its unique slug.
     *
     * @param slug the unique slug of the location to be deleted
     * @return true if the deletion was successful, false if the location was not found
     */
    @Override
    public boolean deleteLocation(String slug) {
        return locationStorage.delete(slug);
    }
}
