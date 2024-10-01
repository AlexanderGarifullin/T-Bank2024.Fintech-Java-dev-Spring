package com.fin.spr.controllers;

import com.fin.spr.annotations.LogExecutionTime;
import com.fin.spr.exceptions.EntityAlreadyExistsException;
import com.fin.spr.interfaces.ILocationController;
import com.fin.spr.interfaces.ILocationService;
import com.fin.spr.models.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * The {@code LocationController} class handles HTTP requests related to {@link Location} entities.
 * It implements the {@link ILocationController} interface and uses the {@link ILocationService}
 * to perform CRUD operations on locations.
 */
@RestController
@RequestMapping("/api/v1/locations")
@LogExecutionTime
public class LocationController implements ILocationController {

    @Autowired
    private ILocationService locationService;

    /**
     * Retrieves all locations stored in the system.
     *
     * @return a list of all {@link Location} entities
     */
    @GetMapping
    @Override
    @ResponseStatus(HttpStatus.OK)
    public List<Location> getAllLocations() {
        return locationService.getAllLocations();
    }

    /**
     * Retrieves a location by its unique slug.
     *
     * @param slug the unique slug of the location to retrieve
     * @return a {@link ResponseEntity} containing the found {@link Location}, or a not found response if not found
     */
    @GetMapping("/{slug}")
    @Override
    public ResponseEntity<Location> getLocationBySlug(@PathVariable String slug) {
        Optional<Location> location = locationService.getLocationBySlug(slug);
        return location.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Creates a new location in the system.
     *
     * @param location the {@link Location} entity to be created
     * @return a {@link ResponseEntity} containing the created {@link Location}, with a status of 201 (Created)
     */
    @PostMapping
    @Override
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        try {
            locationService.createLocation(location);
            return ResponseEntity.status(201).body(location);
        } catch (EntityAlreadyExistsException e) {
            return ResponseEntity.status(409).body(null);
        }
    }

    /**
     * Updates an existing location in the system.
     *
     * @param slug     the unique slug of the location to update
     * @param location the updated {@link Location} entity
     * @return a {@link ResponseEntity} indicating the result of the update operation
     */
    @PutMapping("/{slug}")
    @Override
    public ResponseEntity<Location> updateLocation(@PathVariable String slug, @RequestBody Location location) {
        boolean updated = locationService.updateLocation(slug, location);
        return updated ? ResponseEntity.ok(location) : ResponseEntity.notFound().build();
    }

    /**
     * Deletes a location from the system by its unique slug.
     *
     * @param slug the unique slug of the location to be deleted
     * @return a {@link ResponseEntity} indicating the result of the deletion operation
     */
    @DeleteMapping("/{slug}")
    @Override
    public ResponseEntity<Void> deleteLocation(@PathVariable String slug) {
        boolean deleted = locationService.deleteLocation(slug);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
