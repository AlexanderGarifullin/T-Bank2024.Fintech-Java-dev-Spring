package com.fin.spr.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fin.spr.BaseIntegrationTest;
import com.fin.spr.controllers.payload.LocationPayload;
import com.fin.spr.exceptions.LocationNotFoundException;
import com.fin.spr.models.Location;
import com.fin.spr.repository.jpa.LocationRepository;
import com.fin.spr.services.LocationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
class LocationControllerIntegrationTest extends BaseIntegrationTest {

    private static final String uri = "/api/v1/locations";

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationRepository locationRepository;

    @AfterEach
    void cleanDatabase() {
        locationRepository.deleteAll();
    }

    @Test
    void getAllLocations_notEmpty() throws Exception {
        LocationPayload payload = new LocationPayload("slug1", "Test Location");
        var createdLocation = locationService.createLocation(payload.slug(), payload.name());

        var mvcResponse = mockMvc.perform(get(uri)
                        .header("Authorization", userBearerToken))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        
        var locations = objectMapper.readValue(
                mvcResponse.getContentAsString(),
                new TypeReference<List<Location>>() {
                }
        );

        assertThat(locations).isNotEmpty();
        assertThat(locations).contains(createdLocation);
   }

   @Test
   void getLocationById_success() throws Exception {
       LocationPayload payload = new LocationPayload("slug1", "Test Location");
       var createdLocation = locationService.createLocation(payload.slug(), payload.name());

       var mvcResponse = mockMvc.perform(get(uri + "/" + createdLocation.getId())
                       .header("Authorization", userBearerToken))
               .andExpectAll(
                       status().isOk(),
                       content().contentType(MediaType.APPLICATION_JSON))
               .andReturn()
               .getResponse();

       var location = objectMapper.readValue(mvcResponse.getContentAsString(), Location.class);

       assertThat(location).isEqualTo(createdLocation);
    }

    @Test
    void getLocationById_notFound() throws Exception {
        mockMvc.perform(get(uri + "/88")
                        .header("Authorization", userBearerToken))
                .andExpectAll(
                  status().isNotFound(),
                  content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    void createLocation_success() throws Exception{
        LocationPayload payload = new LocationPayload("slug1", "Test Location");

        var mvcResponse = mockMvc.perform(post(uri)
                        .header("Authorization", userBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        var location = objectMapper.readValue(mvcResponse.getContentAsString(), Location.class);

        assertThat(location.getName()).isEqualTo(payload.name());
        assertThat(location.getSlug()).isEqualTo(payload.slug());

        var locationFromDb = locationService.getLocationById(location.getId());
        assertThat(locationFromDb).isEqualTo(location);
    }

    private static Stream<Arguments> invalidLocationPayloads() {
        return Stream.of(
                // Пустой slug
                Arguments.of(new LocationPayload("", "Valid Name"), "location.request.slug.is_blank"),
                // Слишком короткий slug
                Arguments.of(new LocationPayload("ab", "Valid Name"), "location.request.slug.invalid_size"),
                // Слишком длинный slug
                Arguments.of(new LocationPayload("abcdef", "Valid Name"), "location.request.slug.invalid_size"),
                // Пустой name
                Arguments.of(new LocationPayload("slug1", ""), "location.request.name.is_blank"),
                // Слишком длинный name
                Arguments.of(new LocationPayload("slug2", "A".repeat(51)), "location.request.name.invalid_size")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidLocationPayloads")
    void createLocation_badRequest(LocationPayload locationPayload) throws Exception {
        mockMvc.perform(post(uri)
                        .header("Authorization", userBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationPayload)))
                .andExpectAll(
                        status().isBadRequest());
    }

    @Test
    public void updateLocation_success() throws Exception{
        var oldLocation = locationService.createLocation("slug1", "Old Location");

        LocationPayload newPayload = new LocationPayload("slug2", "New Location");

        var mvcResponse = mockMvc.perform(put(uri + "/{id}", oldLocation.getId())
                        .header("Authorization", userBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPayload)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        var newLocation = objectMapper.readValue(mvcResponse.getContentAsString(), Location.class);

        assertThat(newLocation.getId()).isEqualTo(oldLocation.getId());
        assertThat(newLocation.getSlug()).isEqualTo(newPayload.slug());
        assertThat(newLocation.getName()).isEqualTo(newPayload.name());
    }

    @ParameterizedTest
    @MethodSource("invalidLocationPayloads")
    void updateLocation_badRequest(LocationPayload locationPayload) throws Exception {
        var oldLocation = locationService.createLocation("slug1", "Old Location");

        mockMvc.perform(put(uri + "/{id}", oldLocation.getId())
                        .header("Authorization", userBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationPayload)))
                .andExpectAll(
                        status().isBadRequest());
    }

    @Test
    void updateLocation_notFound() throws Exception {
        LocationPayload newPayload = new LocationPayload("slug2", "New Location");

        var mvcResponse = mockMvc.perform(put(uri + "/88")
                        .header("Authorization", userBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPayload)))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void deleteLocation_success() throws Exception {
        var oldLocation = locationService.createLocation("slug1", "Old Location");

        mockMvc.perform(delete(uri + "/" + oldLocation.getId())
                        .header("Authorization", userBearerToken))
                .andExpect(status().isNoContent());

        assertThatThrownBy(() -> locationService.getLocationById(oldLocation.getId()))
                .isInstanceOf(LocationNotFoundException.class)
                .hasMessage("location.not_found");
    }

    @Test
    void deleteLocation_notFound() throws Exception {
        mockMvc.perform(delete(uri + "/88")
                        .header("Authorization", userBearerToken))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
}