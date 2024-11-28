package com.fin.spr.controllers;

import com.fin.spr.annotations.LogExecutionTime;
import com.fin.spr.controllers.payload.EventPayload;
import com.fin.spr.exceptions.LocationNotFoundException;
import com.fin.spr.models.Event;
import com.fin.spr.models.Location;
import com.fin.spr.services.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@LogExecutionTime
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        try {
            return  ResponseEntity.ok(eventService.getEventById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody EventPayload event) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(eventService.createEvent(event.name(), event.startDate(), event.price(), event.free(),
                            event.locationId()));
        } catch (LocationNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id,
                      @Valid @RequestBody EventPayload event) {
        try {
            return  ResponseEntity.ok(eventService.updateEvent(id, event.name(), event.startDate(), event.price(),
                    event.free(), event.locationId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(null);
        }
    }

    @GetMapping("/filter")
    public List<Event> searchEventWithQuery(@RequestParam(required = false) String name,
                                     @RequestParam(required = false) Location location,
                                     @RequestParam(required = false) Instant fromDate,
                                     @RequestParam(required = false) Instant toDate) {
        return eventService.searchEventWithQuery(name, location, fromDate, toDate);
    }
}
