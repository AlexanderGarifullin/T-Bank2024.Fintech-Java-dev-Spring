package com.fin.spr.services;

import com.fin.spr.models.Location;
import com.fin.spr.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LocationServiceTest {

    @InjectMocks
    private LocationService locationService;

    @Mock
    private InMemoryStorage<Location, String> locationStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllLocations() {
        Location location1 = new Location("slug1", "Location 1");
        Location location2 = new Location("slug2", "Location 2");

        when(locationStorage.getAll()).thenReturn(List.of(location1, location2));

        List<Location> locations = locationService.getAllLocations();
        assertThat(locations).hasSize(2)
                .containsExactly(location1, location2);
    }

    @Test
    void testGetLocationBySlug() {
        Location location = new Location("slug1", "Location 1");

        when(locationStorage.getById("slug1")).thenReturn(Optional.of(location));

        Optional<Location> foundLocation = locationService.getLocationBySlug("slug1");
        assertThat(foundLocation).isPresent()
                .hasValue(location);
    }

    @Test
    void testCreateLocation() {
        Location location = new Location("slug1", "Location 1");

        locationService.createLocation(location);

        verify(locationStorage, times(1)).create("slug1", location);
    }

    @Test
    void testUpdateLocation() {
        Location location = new Location("slug1", "Location 1");
        Location updatedLocation = new Location("slug1", "Updated Location");

        when(locationStorage.update("slug1", updatedLocation)).thenReturn(true);

        boolean result = locationService.updateLocation("slug1", updatedLocation);

        assertThat(result).isTrue();
        verify(locationStorage, times(1)).update("slug1", updatedLocation);
    }

    @Test
    void testDeleteLocation() {
        when(locationStorage.delete("slug1")).thenReturn(true);

        boolean result = locationService.deleteLocation("slug1");

        assertThat(result).isTrue();
        verify(locationStorage, times(1)).delete("slug1");
    }

    @Test
    void testDeleteLocationNotFound() {
        when(locationStorage.delete("slug2")).thenReturn(false);

        boolean result = locationService.deleteLocation("slug2");

        assertThat(result).isFalse();
        verify(locationStorage, times(1)).delete("slug2");
    }
}

// ls7