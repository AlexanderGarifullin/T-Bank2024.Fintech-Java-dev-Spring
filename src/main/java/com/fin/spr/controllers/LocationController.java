package com.fin.spr.controllers;

import com.fin.spr.annotations.LogExecutionTime;
import com.fin.spr.controllers.payload.LocationPayload;
import com.fin.spr.interfaces.service.ILocationService;
import com.fin.spr.models.Location;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/locations")
@LogExecutionTime
public class LocationController{

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
    public ResponseEntity<Location>  getLocationById(@PathVariable Long id) {
        try {
            return  ResponseEntity.ok(locationService.getLocationById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@Valid @RequestBody LocationPayload location) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(locationService.createLocation(location.slug(), location.name()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location>  updateLocation(@PathVariable Long id,
                                      @Valid @RequestBody LocationPayload location) {
        try {
            return  ResponseEntity.ok(locationService.updateLocation(id, location.slug(), location.name()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        try {
            locationService.deleteLocation(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(null);
        }
    }
}
