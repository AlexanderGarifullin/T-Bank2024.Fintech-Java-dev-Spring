package com.fin.spr.services;

import com.fin.spr.exceptions.EntityNotFoundException;
import com.fin.spr.exceptions.LocationNotFoundException;
import com.fin.spr.interfaces.ILocationService;
import com.fin.spr.models.Location;
import com.fin.spr.repository.jpa.LocationRepository;
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
    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Location getLocationById(Long id) {
        return locationRepository.findById(id).orElseThrow(() ->
                new LocationNotFoundException(id));
    }

    @Override
    public Location createLocation(String slug, String name) {
        Location location = new Location();
        location.setName(name);
        location.setSlug(slug);
        return locationRepository.save(location);
    }

    @Override
    public Location updateLocation(Long id, String slug, String name) {
        var oldLocation = locationRepository.findById(id).orElseThrow(() ->
                new LocationNotFoundException(id));

        oldLocation.setSlug(slug);
        oldLocation.setSlug(name);
        return locationRepository.save(oldLocation);
    }

    @Override
    public void deleteLocation(Long id) {
        if (locationRepository.existsById(id)) {
            locationRepository.deleteById(id);
            return;
        }
        throw new LocationNotFoundException(id);
    }
}
