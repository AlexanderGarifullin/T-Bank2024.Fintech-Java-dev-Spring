package com.fin.spr.interfaces.controller;

import com.fin.spr.controllers.payload.LocationPayload;
import com.fin.spr.models.Location;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The {@code ILocationController} interface defines the contract for managing location-related operations
 * in the application. It provides methods for performing CRUD operations on {@link Location} entities.
 */
public interface ILocationController {

    List<Location> getAllLocations();

    Location getLocationById(@PathVariable Long id);

    ResponseEntity<Location> createLocation(@Valid @RequestBody LocationPayload location);

    Location updateLocation(@PathVariable Long id,
                                   @Valid @RequestBody LocationPayload location);
    ResponseEntity<Void> deleteLocation(@PathVariable Long id);
}
