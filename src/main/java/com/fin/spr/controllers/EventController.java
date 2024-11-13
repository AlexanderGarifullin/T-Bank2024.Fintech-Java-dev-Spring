package com.fin.spr.controllers;

import com.fin.spr.annotations.LogExecutionTime;
import com.fin.spr.controllers.payload.EventPayload;
import com.fin.spr.models.Event;
import com.fin.spr.models.Location;
import com.fin.spr.services.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public List<Event> getAllEvents(){
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody EventPayload event){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(event.name(), event.startDate(), event.price(), event.free(), event.locationId()));
    }

    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id,
                      @Valid @RequestBody EventPayload event){
        return eventService.updateEvent(id, event.name(), event.startDate(), event.price(), event.free(), event.locationId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id){
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public List<Event> searchEventWithQuery(@RequestParam(required = false) String name,
                                     @RequestParam(required = false) Location location,
                                     @RequestParam(required = false) Instant fromDate,
                                     @RequestParam(required = false) Instant toDate) {
        return eventService.searchEventWithQuery(name, location, fromDate, toDate);
    }
}
