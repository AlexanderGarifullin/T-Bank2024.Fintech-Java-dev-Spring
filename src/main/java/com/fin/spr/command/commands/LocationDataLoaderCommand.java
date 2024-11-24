package com.fin.spr.command.commands;

import com.fin.spr.command.DataLoaderCommand;
import com.fin.spr.controllers.payload.LocationPayload;
import com.fin.spr.models.response.EventResponse;
import com.fin.spr.models.response.EventsResponse;
import com.fin.spr.services.EventService;
import com.fin.spr.services.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class LocationDataLoaderCommand implements DataLoaderCommand {

    private static final String Fields = "title,dates,price,is_free,location";

    @Value("${kudago.api.locations.url}")
    private String LOCATIONS_API_URL;

    @Value("${kudago.api.events.url}")
    private String EVENTS_API_URL;
    @Value("${kudago.limits.events.page_size}")
    private int PAGE_SIZE;

    @Value("${kudago.limits.events.max_page}")
    private int MAX_PAGE;

    private final RestClient restClient;
    private final LocationService locationService;
    private final EventService eventService;

    @Autowired
    public LocationDataLoaderCommand(RestClient restClient, LocationService locationService, EventService eventService) {
        this.restClient = restClient;
        this.locationService = locationService;
        this.eventService = eventService;
    }


    @Override
    public void execute() {
        initLocations();
        initEvents();
    }

    private void initLocations() {
        List<LocationPayload> locations = restClient.get()
                .uri(LOCATIONS_API_URL)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (locations != null) {
            locationService.initLocations(locations);
            log.info("Locations have been successfully initialized.");
        } else {
            log.warn("Received null locations.");
        }
    }

    private void initEvents() {
        List<EventsResponse> allEventsResponse = fetchAllEvents();
        log.info("Get " + allEventsResponse.size() + " events pages");
        if (!allEventsResponse.isEmpty()) {
            List<EventResponse> eventsToAdd = new ArrayList<>();
            allEventsResponse.forEach(eventsResponse -> eventsToAdd.addAll(eventsResponse.getResults()));
            eventService.initEvents(eventsToAdd);
            log.info("Events have been successfully initialized.");
        } else {
            log.warn("Received null events.");
        }
    }



    private List<EventsResponse> fetchAllEvents(){
        int page = 1;
        List<EventsResponse> eventsResponses = new ArrayList<>();
        while (page <= MAX_PAGE) {
            var events = fetchEvents(page++);
            if (events == null) break;
            eventsResponses.add(events);
        }
        return eventsResponses;
    }
    private EventsResponse fetchEvents(int page) {
        log.info("Get events from page " + page);
        try {
            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EVENTS_API_URL)
                            .queryParam("page", page)
                            .queryParam("page_size", PAGE_SIZE)
                            .queryParam("fields", Fields)
                            .build())
                    .retrieve()
                    .toEntity(EventsResponse.class);
            if (!(response.getStatusCode().is2xxSuccessful() && response.getBody() != null)) {
                log.info("Get nothing from page" + page);
                return null;
            }
            log.info("Get successfull from page" + page);
            log.info("Cnt elements = " + response.getBody().getResults().size());
            return response.getBody();
        } catch (Exception ex) {
            log.info("(ex) Get nothing from page" + page);
            log.error(ex.getMessage());
            return null;
        }
    }
}
