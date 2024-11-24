package com.fin.spr.services;

import com.fin.spr.controllers.payload.LocationPayload;
import com.fin.spr.exceptions.LocationNotFoundException;
import com.fin.spr.interfaces.service.ILocationService;
import com.fin.spr.interfaces.service.observer.Subject;
import com.fin.spr.models.Location;
import com.fin.spr.repository.jpa.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The {@code LocationService} class provides the implementation of the {@link ILocationService} interface
 * for managing locations. It utilizes an in-memory storage solution to perform CRUD operations
 * on {@link Location} entities.
 */

@Slf4j
@Service
public class LocationService implements ILocationService {
    private final LocationRepository locationRepository;
    private final Subject messagePublisher;

    @Autowired
    public LocationService(LocationRepository locationRepository, Subject messagePublisher) {
        this.locationRepository = locationRepository;
        this.messagePublisher = messagePublisher;
    }

    public void initLocations(List<LocationPayload> locations) {
        locations.forEach(payload -> {
            if (!locationRepository.existsBySlug(payload.slug())) {
                Location location = new Location();
                location.setSlug(payload.slug());
                location.setName(payload.name());
                locationRepository.save(location);
            }
        });
        log.info("Locations added to data base!");
    }

    @Override
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Location getLocationById(Long id){
        return locationRepository.findById(id).orElseThrow(() ->
                new LocationNotFoundException(id));
    }

    @Override
    public Location createLocation(String slug, String name) {
        Location location = new Location();
        location.setName(name);
        location.setSlug(slug);
        messagePublisher.notifyUpdate("Location created: " + location.getName());
        return locationRepository.save(location);
    }

    @Override
    public Location updateLocation(Long id, String slug, String name) {
        var oldLocation = locationRepository.findById(id).orElseThrow(() ->
                new LocationNotFoundException(id));

        oldLocation.setSlug(slug);
        oldLocation.setName(name);
        messagePublisher.notifyUpdate("Location updated: " + oldLocation.getName());
        return locationRepository.save(oldLocation);
    }

    @Override
    public void deleteLocation(Long id) throws LocationNotFoundException{
        if (locationRepository.existsById(id)) {
            locationRepository.deleteById(id);
            messagePublisher.notifyUpdate("Location deleted with ID: " + id);
            return;
        }
        throw new LocationNotFoundException(id);
    }
}
