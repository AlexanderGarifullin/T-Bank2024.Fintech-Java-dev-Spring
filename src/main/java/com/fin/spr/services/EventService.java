package com.fin.spr.services;

import com.fin.spr.exceptions.EventNotFoundException;
import com.fin.spr.exceptions.LocationNotFoundException;
import com.fin.spr.models.Event;
import com.fin.spr.models.Location;
import com.fin.spr.models.response.EventResponse;
import com.fin.spr.repository.jpa.EventRepository;
import com.fin.spr.repository.jpa.LocationRepository;
import com.fin.spr.repository.specification.EventSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.TreeMap;

@Slf4j
@Service
public class EventService {

    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;

    @Autowired
    public EventService(LocationRepository locationRepository,
                        EventRepository eventRepository) {
        this.locationRepository = locationRepository;
        this.eventRepository = eventRepository;
    }

    public void initEvents(List<EventResponse> events) {
        var locations = locationRepository.findAll();

        TreeMap<String, Location> slugIdLocationMap = new TreeMap<>();
        locations.forEach(location -> slugIdLocationMap.put(location.getSlug(), location));

        for (var event : events) {
            if (slugIdLocationMap.containsKey(event.getLocation().getSlug())) {
                Event eventToAdd = new Event();
                eventToAdd.setName(event.getTitle());
                eventToAdd.setFree(event.isFree());
                eventToAdd.setPrice(event.getPrice());
                eventToAdd.setLocation(slugIdLocationMap.get(event.getLocation().getSlug()));
                eventToAdd.setStartDate(event.getDates().getFirst().getStart());
                eventRepository.save(eventToAdd);
            }
        }
        log.info("Events added to data base!");
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAllWithLocations();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new EventNotFoundException(id));
    }

    public Event createEvent(String name, Instant startDate, String price, boolean free, Long locationId) {
        Event event = new Event();
        event.setName(name);
        event.setStartDate(startDate);
        event.setPrice(price);
        event.setFree(free);
        var location = locationRepository.findById(locationId);
        if (location.isPresent()) {
            event.setLocation(location.get());
            return eventRepository.save(event);
        } else {
            throw new LocationNotFoundException(locationId);
        }
    }

    public Event updateEvent(Long id, String name, Instant startDate, String price, boolean free, Long locationId) {
        var oldEvent = eventRepository.findById(id).orElseThrow(() ->
                new EventNotFoundException(id));

        var location = locationRepository.findById(locationId);
        if (location.isPresent()) {
            oldEvent.setLocation(location.get());
        } else {
            throw new LocationNotFoundException(locationId);
        }

        oldEvent.setName(name);
        oldEvent.setStartDate(startDate);
        oldEvent.setPrice(price);
        oldEvent.setFree(free);

        return eventRepository.save(oldEvent);
    }

    public void deleteEvent(Long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return;
        }
        throw new EventNotFoundException(id);
    }

    public List<Event> searchEventWithQuery(String name, Location location, Instant fromDate, Instant toDate) {
        var specification = EventSpecifications.buildSpecification(name, location, fromDate, toDate);
        return eventRepository.findAll(specification);
    }
}
