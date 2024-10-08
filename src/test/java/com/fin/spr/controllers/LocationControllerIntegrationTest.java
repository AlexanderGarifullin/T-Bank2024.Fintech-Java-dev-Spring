package com.fin.spr.controllers;

import com.fin.spr.exceptions.EntityAlreadyExistsException;
import com.fin.spr.models.Location;
import com.fin.spr.services.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LocationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Location testLocation1;
    private Location testLocation2;

    private static final String BASE_URL = "/api/v1/locations";

    @BeforeEach
    public void setup() {
        testLocation1 = new Location("paris", "Paris");
        testLocation2 = new Location("london", "London");
    }

    @Test
    public void testGetAllLocations() throws Exception {
        Mockito.when(locationService.getAllLocations()).thenReturn(Arrays.asList(testLocation1, testLocation2));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Paris"))
                .andExpect(jsonPath("$[1].name").value("London"));
    }

    @Test
    public void testGetLocationBySlug_Success() throws Exception {
        Mockito.when(locationService.getLocationBySlug("paris")).thenReturn(Optional.of(testLocation1));

        mockMvc.perform(get(BASE_URL + "/paris"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Paris"))
                .andExpect(jsonPath("$.slug").value("paris"));
    }

    @Test
    public void testGetLocationBySlug_NotFound() throws Exception {
        Mockito.when(locationService.getLocationBySlug("berlin")).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/berlin"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateLocation_Success() throws Exception {
        Mockito.doNothing().when(locationService).createLocation(any(Location.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLocation1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Paris"))
                .andExpect(jsonPath("$.slug").value("paris"));
    }

    @Test
    public void testCreateLocation_Conflict() throws Exception {
        Mockito.doThrow(new EntityAlreadyExistsException("Location already exists")).when(locationService).createLocation(any(Location.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLocation1)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateLocation_Success() throws Exception {
        Mockito.when(locationService.updateLocation(eq("paris"), any(Location.class))).thenReturn(true);

        mockMvc.perform(put(BASE_URL + "/paris")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLocation1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Paris"))
                .andExpect(jsonPath("$.slug").value("paris"));
    }

    @Test
    public void testUpdateLocation_NotFound() throws Exception {
        Mockito.when(locationService.updateLocation(eq("berlin"), any(Location.class))).thenReturn(false);

        mockMvc.perform(put(BASE_URL + "/berlin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLocation1)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteLocation_Success() throws Exception {
        Mockito.when(locationService.deleteLocation("paris")).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/paris"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteLocation_NotFound() throws Exception {
        Mockito.when(locationService.deleteLocation("berlin")).thenReturn(false);

        mockMvc.perform(delete(BASE_URL + "/berlin"))
                .andExpect(status().isNotFound());
    }
}

// ls7