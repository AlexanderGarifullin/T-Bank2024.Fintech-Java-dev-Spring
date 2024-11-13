package com.fin.spr.services;

import com.fin.spr.annotations.LogExecutionTime;
import com.fin.spr.controllers.payload.LocationPayload;
import com.fin.spr.models.Category;
import com.fin.spr.models.Event;
import com.fin.spr.models.Location;
import com.fin.spr.models.response.EventResponse;
import com.fin.spr.models.response.EventsResponse;
import com.fin.spr.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The {@code KudaGoSerivce} class is responsible for periodically loading data from the KudaGo API,
 * specifically categories and locations, and storing them in in-memory storage.
 * <p>
 * This service supports parallel processing for efficient data loading, using {@link ExecutorService}
 * and {@link ScheduledExecutorService}. It retrieves data using a {@link RestClient}.
 * </p>
 *
 * @see Category
 * @see Location
 * @see InMemoryStorage
 * @see LogExecutionTime
 *
 * @version 1.0
 */

@Service
public class KudaGoSerivce  {
    private static final Logger logger = LoggerFactory.getLogger(KudaGoSerivce.class);
    private static final String Fields = "title,dates,price,is_free,location";

    @Value("${kudago.api.categories.url}")
    private String CATEGORIES_API_URL;

    @Value("${kudago.api.locations.url}")
    private String LOCATIONS_API_URL;

    @Value("${kudago.api.events.url}")
    private String EVENTS_API_URL;

    @Value("${app.init.schedule-duration}")
    private Duration scheduleDuration;

    @Value("${kudago.limits.events.page_size}")
    private int PAGE_SIZE;

    @Value("${kudago.limits.events.max_page}")
    private int MAX_PAGE;

    private final RestClient restClient;
    private InMemoryStorage<Category, Integer> categoryStorage;
    private final ExecutorService fixedThreadPool;
    private final ScheduledExecutorService scheduledThreadPool;
    private final LocationService locationService;
    private final EventService eventService;


    public KudaGoSerivce(RestClient restClient,
                         InMemoryStorage<Category, Integer> categoryStorage,
                         LocationService locationService,
                         EventService eventService,
                         @Qualifier("fixedThreadPool") ExecutorService fixedThreadPool,
                         @Qualifier("scheduledThreadPool") ScheduledExecutorService scheduledThreadPool) {
        this.restClient = restClient;
        this.categoryStorage = categoryStorage;
        this.locationService = locationService;
        this.eventService = eventService;
        this.fixedThreadPool = fixedThreadPool;
        this.scheduledThreadPool = scheduledThreadPool;
    }

    /**
     * Initiates the periodic data loading process, scheduling data updates based on the specified {@code scheduleDuration}.
     * <p>
     * This method runs at fixed intervals defined by {@code scheduleDuration} and triggers the parallel loading of categories
     * and locations using the {@link #parallelInitData()} method.
     * </p>
     */
    public void initData() {
        scheduledThreadPool.scheduleAtFixedRate(
                this::parallelInitData,
                0,
                scheduleDuration.toMinutes(),
                TimeUnit.MINUTES);
    }

    /**
     * Executes the data loading tasks in parallel.
     * <p>
     * This method runs data loading tasks for categories and locations concurrently, ensuring efficient initialization.
     * Logs a completion message upon successful initialization.
     * </p>
     */
    public void parallelInitData() {
        try {
            fixedThreadPool.invokeAll(List.of(
                    () -> { loadCategories(); return null; },
                    () -> { loadLocations(); return null; } ,
                    () -> { loadEvents(); return null; }
            ));
            logger.info("Data initialization completed with invokeAll.");
        } catch (InterruptedException e) {
            logger.error("Data initialization interrupted.", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Loads categories from the KudaGo API and stores them in {@link InMemoryStorage}.
     * <p>
     * If no data is returned, a warning message is logged.
     * </p>
     */
    private void loadCategories() {
        List<Category> categories = restClient.get()
                .uri(CATEGORIES_API_URL)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (categories != null) {
            categories.forEach(category -> categoryStorage.create(category.getId(), category));
            logger.info("Categories have been successfully initialized.");
        } else {
            logger.warn("Received null categories.");
        }
    }

    private void loadLocations() {
        List<LocationPayload> locations = restClient.get()
                .uri(LOCATIONS_API_URL)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (locations != null) {
            locationService.initLocations(locations);
            logger.info("Locations have been successfully initialized.");
        } else {
            logger.warn("Received null locations.");
        }
    }

    private void loadEvents() {
        List<EventsResponse> allEventsResponse = fetchAllEvents();
        logger.info("Get " + allEventsResponse.size() + " events pages");
        if (!allEventsResponse.isEmpty()) {
            List<EventResponse> eventsToAdd = new ArrayList<>();
            allEventsResponse.forEach(eventsResponse -> eventsToAdd.addAll(eventsResponse.getResults()));
            eventService.initEvents(eventsToAdd);
            logger.info("Events have been successfully initialized.");
        } else {
            logger.warn("Received null events.");
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
        logger.info("Get events from page " + page);
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
                logger.info("Get nothing from page" + page);
                return null;
            }
            logger.info("Get successfull from page" + page);
            logger.info("Cnt elements = " + response.getBody().getResults().size());
            return response.getBody();
        } catch (Exception ex) {
            logger.info("(ex) Get nothing from page" + page);
            logger.error(ex.getMessage());
            return null;
        }
    }
}
