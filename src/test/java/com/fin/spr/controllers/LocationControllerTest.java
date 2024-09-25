package com.fin.spr.controllers;

import com.fin.spr.exceptions.EntityAlreadyExistsException;
import com.fin.spr.interfaces.ILocationService;
import com.fin.spr.models.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LocationControllerTest {

    @InjectMocks
    private LocationController locationController;

    @Mock
    private ILocationService locationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllLocations() {
        Location location1 = new Location("slug 1", "Location 1");
        Location location2 = new Location("slug 2", "Location 2");

        when(locationService.getAllLocations()).thenReturn(List.of(location1, location2));

        ResponseEntity<List<Location>> response = locationController.getAllLocations();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(2)
                .containsExactly(location1, location2);
    }

    @Test
    void testGetLocationByIdFound() {
        Location location = new Location("slug 1", "Location 1");

        when(locationService.getLocationBySlug("slug 1")).thenReturn(Optional.of(location));

        ResponseEntity<Location> response = locationController.getLocationBySlug("slug 1");
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(location);
    }

    @Test
    void testGetLocationByIdNotFound() {
        when(locationService.getLocationBySlug("slug 1")).thenReturn(Optional.empty());

        ResponseEntity<Location> response = locationController.getLocationBySlug("slug 1");
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void testCreateLocationSuccess() {
        Location location = new Location("slug 1", "Location 1");

        doNothing().when(locationService).createLocation(location);

        ResponseEntity<Location> response = locationController.createLocation(location);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isEqualTo(location);
    }

    @Test
    void testCreateLocationAlreadyExists() {
        Location location = new Location("slug 1", "Location 1");

        doThrow(EntityAlreadyExistsException.class).when(locationService).createLocation(location);

        ResponseEntity<Location> response = locationController.createLocation(location);

        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void testUpdateLocationSuccess() {
        Location updatedLocation = new Location("Updated slug", "Updated Location");

        when(locationService.updateLocation("slug", updatedLocation)).thenReturn(true);

        ResponseEntity<Location> response = locationController.updateLocation("slug", updatedLocation);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(updatedLocation);
    }

    @Test
    void testUpdateLocationNotFound() {
        Location updatedLocation = new Location("Updated slug", "Updated Location");

        when(locationService.updateLocation("slug", updatedLocation)).thenReturn(false);

        ResponseEntity<Location> response = locationController.updateLocation("slug", updatedLocation);
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void testDeleteLocationSuccess() {
        when(locationService.deleteLocation("slug")).thenReturn(true);

        ResponseEntity<Void> response = locationController.deleteLocation("slug");
        assertThat(response.getStatusCodeValue()).isEqualTo(204);
    }

    @Test
    void testDeleteLocationNotFound() {
        when(locationService.deleteLocation("slug")).thenReturn(false);

        ResponseEntity<Void> response = locationController.deleteLocation("slug");
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }
}
