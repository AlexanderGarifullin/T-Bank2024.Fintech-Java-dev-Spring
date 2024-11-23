package com.fin.spr.interfaces.service;

import com.fin.spr.models.Location;

import java.util.List;

/**
 * The {@code ILocationService} interface defines the contract for services
 * managing {@link Location} entities. It provides methods to perform
 * operations such as retrieving, creating, updating, and deleting locations.
 */
public interface ILocationService {


    List<Location> getAllLocations();


    Location getLocationById(Long id);


    Location createLocation(String slug, String name);


    Location updateLocation(Long id, String slug, String name);


    void deleteLocation(Long id);
}
