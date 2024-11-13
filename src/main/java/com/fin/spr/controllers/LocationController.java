package com.fin.spr.controllers;

import com.fin.spr.annotations.LogExecutionTime;
import com.fin.spr.controllers.payload.LocationPayload;
import com.fin.spr.interfaces.controller.ILocationController;
import com.fin.spr.interfaces.service.ILocationService;
import com.fin.spr.models.Location;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The {@code LocationController} class handles HTTP requests related to {@link Location} entities.
 * It implements the {@link ILocationController} interface and uses the {@link ILocationService}
 * to perform CRUD operations on locations.
 */
@RestController
@RequestMapping("/api/v1/locations")
@LogExecutionTime
public class LocationController implements ILocationController{

    private final ILocationService locationService;


    @Autowired
    public LocationController(ILocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public List<Location> getAllLocations() {
        return locationService.getAllLocations();
    }

    @GetMapping("/{id}")
    public Location getLocationById(@PathVariable Long id) {
        return locationService.getLocationById(id);
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@Valid @RequestBody LocationPayload location) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(locationService.createLocation(location.slug(), location.name()));
    }

    @PutMapping("/{id}")
    public Location updateLocation(@PathVariable Long id,
                                      @Valid @RequestBody LocationPayload location) {
        return locationService.updateLocation(id, location.slug(), location.name());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
