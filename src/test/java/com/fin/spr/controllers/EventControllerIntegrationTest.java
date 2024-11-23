package com.fin.spr.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fin.spr.BaseIntegrationTest;
import com.fin.spr.controllers.payload.EventPayload;
import com.fin.spr.exceptions.EventNotFoundException;
import com.fin.spr.models.Event;
import com.fin.spr.models.Location;
import com.fin.spr.repository.jpa.EventRepository;
import com.fin.spr.repository.jpa.LocationRepository;
import com.fin.spr.services.EventService;
import com.fin.spr.services.LocationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerIntegrationTest extends BaseIntegrationTest {

    private static final String events_uri = "/api/v1/events";
    private static final String locations_uri = "/api/v1/locations";

    @Autowired
    private LocationService locationService;

    @Autowired
    private EventService eventService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private EventRepository eventRepository;

    private static EventPayload eventPayload;

    @BeforeAll
    static void init() {
        eventPayload = new EventPayload("name",
                Instant.now().truncatedTo(ChronoUnit.MINUTES),
                "price",
                false,
                1L);
    }

    @AfterEach
    void cleanDatabase() {
        locationRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    public void getAllEvents_notEmpty() throws Exception {
        var createdLocation = locationService.createLocation("test", "Test Location");
        var createdEvent = eventService.createEvent(eventPayload.name(), eventPayload.startDate(),
                eventPayload.price(), eventPayload.free(), createdLocation.getId());

        var mvcResponse = mockMvc.perform(get(events_uri))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        var events = objectMapper.readValue(
                mvcResponse.getContentAsString(),
                new TypeReference<List<Event>>() {
                }
        );

        assertThat(events).isNotEmpty();
        assertThat(events).contains(createdEvent);
    }

    @Test
    void getEventById_success() throws Exception {
        var createdLocation = locationService.createLocation("test", "Test Location");
        var createdEvent = eventService.createEvent(eventPayload.name(), eventPayload.startDate(),
                eventPayload.price(), eventPayload.free(), createdLocation.getId());

        var mvcResponse = mockMvc.perform(get(events_uri + "/{id}", createdEvent.getId()))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        var event = objectMapper.readValue(mvcResponse.getContentAsString(), Event.class);
        assertAll(
                () -> assertThat(event.getPrice()).isEqualTo(createdEvent.getPrice()),
                () -> assertThat(event.getName()).isEqualTo(createdEvent.getName()),
                () -> assertThat(event.getStartDate()).isEqualTo(createdEvent.getStartDate())
        );
    }

    @Test
    void getEventById_notFound() throws Exception {
        mockMvc.perform(get(events_uri + "/88"))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }


    @Test
    void createEvent_success() throws Exception {
        var createdLocation = locationService.createLocation("test", "Test Location");

        eventPayload = new EventPayload(eventPayload.name(), eventPayload.startDate(), eventPayload.price(),
                eventPayload.free(), createdLocation.getId());

        var mvcResponse = mockMvc.perform(post(events_uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventPayload)))
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        var event = objectMapper.readValue(mvcResponse.getContentAsString(), Event.class);

        assertAll(
                () -> assertThat(event.getStartDate()).isEqualTo(eventPayload.startDate()),
                () -> assertThat(event.getName()).isEqualTo(eventPayload.name()),
                () -> assertThat(event.getPrice()).isEqualTo(eventPayload.price()),
                () -> assertThat(event.isFree()).isEqualTo(eventPayload.free())
        );
    }


    private static Stream<Arguments> invalidEventPayloads() {
        return Stream.of(
                // Нет имени
                Arguments.of(new EventPayload(null,
                        Instant.now().truncatedTo(ChronoUnit.MINUTES),
                        "price",
                        false,
                        1L), "event.request.name.is_blank"),
                // Нет даты
                Arguments.of(new EventPayload("name",
                        null,
                        "price",
                        false,
                        1L), "event.request.startDate.is_null")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidEventPayloads")
    void createEvent_badRequest(EventPayload badEventPayload) throws Exception {
        mockMvc.perform(post(events_uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badEventPayload)))
                .andExpectAll(
                        status().isBadRequest());
    }

    @Test
    void createEvent_locationNotFound() throws Exception {
        eventPayload = new EventPayload(eventPayload.name(), eventPayload.startDate(), eventPayload.price(),
                eventPayload.free(), -1L);

        var mvcResponse = mockMvc.perform(post(events_uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventPayload)))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void updateEvent_success() throws Exception {
        var createdLocation = locationService.createLocation("test", "Test Location");
        var createdEvent = eventService.createEvent(eventPayload.name(), eventPayload.startDate(),
                eventPayload.price(), eventPayload.free(), createdLocation.getId());

        eventPayload = new EventPayload("new name",
                Instant.now().truncatedTo(ChronoUnit.MINUTES),
                "new price",
                true,
                createdLocation.getId());

        var mvcResponse = mockMvc.perform(put(events_uri + "/{id}", createdEvent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventPayload)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        var event = objectMapper.readValue(mvcResponse.getContentAsString(), Event.class);
        assertAll(
                () -> assertThat(event.getPrice()).isEqualTo(eventPayload.price()),
                () -> assertThat(event.getName()).isEqualTo(eventPayload.name()),
                () -> assertThat(event.getStartDate()).isEqualTo(eventPayload.startDate()),
                () -> assertThat(event.isFree()).isEqualTo(eventPayload.free())
        );
    }

    @ParameterizedTest
    @MethodSource("invalidEventPayloads")
    void updateEvent_badRequest(EventPayload badEventPayload) throws Exception {
        var createdLocation = locationService.createLocation("test", "Test Location");
        var createdEvent = eventService.createEvent(eventPayload.name(), eventPayload.startDate(),
                eventPayload.price(), eventPayload.free(), createdLocation.getId());

        mockMvc.perform(put(events_uri + "/{id}", createdEvent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badEventPayload)))
                .andExpectAll(
                        status().isBadRequest());
    }

    @Test
    void updateEvent_notFound() throws Exception {
        var mvcResponse = mockMvc.perform(put(events_uri + "/8888")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventPayload)))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }


    @Test
    void deleteEvent_success() throws Exception {
        var createdLocation = locationService.createLocation("test", "Test Location");
        var createdEvent = eventService.createEvent(eventPayload.name(), eventPayload.startDate(),
                eventPayload.price(), eventPayload.free(), createdLocation.getId());

        mockMvc.perform(delete(events_uri + "/" + createdEvent.getId()))
                .andExpect(status().isNoContent());

        assertThatThrownBy(() -> eventService.getEventById(createdEvent.getId()))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessage("event.not_found");
    }

    @Test
    void deleteEvent_notFound() throws Exception {
        mockMvc.perform(delete(events_uri + "/88888"))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void getLocationWithEvents_success() throws Exception {
        var createdLocation = locationService.createLocation("test", "Test Location");
        var createdEvent = eventService.createEvent(eventPayload.name(), eventPayload.startDate(),
                eventPayload.price(), eventPayload.free(), createdLocation.getId());

        var mvcResponse = mockMvc.perform(get(locations_uri + "/" + createdLocation.getId()))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        var location = objectMapper.readValue(mvcResponse.getContentAsString(), Location.class);

        assertThat(location).isEqualTo(createdLocation);
        assertThat(location.getEvents())
                .isNotEmpty()
                .contains(createdEvent);
    }

    @Test
    void searchEvent_withParams() throws Exception {
        var createdLocation = locationService.createLocation("test", "Test Location");
        var createdEvent = eventService.createEvent(eventPayload.name(), eventPayload.startDate(),
                eventPayload.price(), eventPayload.free(), createdLocation.getId());

        var mvcResponse = mockMvc.perform(get(events_uri + "/filter")
                        .param("name", createdEvent.getName())
                        .param("fromDate", createdEvent.getStartDate().minus(Duration.ofDays(1)).toString())
                        .param("toDate", createdEvent.getStartDate().plus(Duration.ofDays(1)).toString()))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        var events = objectMapper.readValue(mvcResponse.getContentAsString(),
                new TypeReference<List<Event>>() {
                });

        assertThat(events).isNotEmpty()
                .hasSize(1);

        assertThat(events.getFirst()).isEqualTo(createdEvent);
    }

    @Test
    void searchEvent_withNullParams() throws Exception {
        var createdLocation = locationService.createLocation("test", "Test Location");
        var createdEvent = eventService.createEvent(eventPayload.name(), eventPayload.startDate(),
                eventPayload.price(), eventPayload.free(), createdLocation.getId());

        var mvcResponse = mockMvc.perform(get(events_uri + "/filter"))
                 .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        var events = objectMapper.readValue(mvcResponse.getContentAsString(),
                new TypeReference<List<Event>>() {
                });

        assertThat(events).isNotEmpty()
                .hasSize(1);

        assertThat(events.getFirst()).isEqualTo(createdEvent);
    }

    @Test
    void searchEvent_returnEmptyList() throws Exception {
        var createdLocation = locationService.createLocation("test", "Test Location");
        var createdEvent = eventService.createEvent(eventPayload.name(), eventPayload.startDate(),
                eventPayload.price(), eventPayload.free(), createdLocation.getId());

        var mvcResponse = mockMvc.perform(get(events_uri + "/filter")
                        .param("name", createdEvent.getName())
                        .param("fromDate", createdEvent.getStartDate().plus(Duration.ofDays(10)).toString()))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        var events = objectMapper.readValue(mvcResponse.getContentAsString(),
                new TypeReference<List<Event>>() {
                });

        assertThat(events).isEmpty();
    }
}