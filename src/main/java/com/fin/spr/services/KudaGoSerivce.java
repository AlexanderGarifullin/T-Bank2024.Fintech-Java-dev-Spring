package com.fin.spr.services;

import com.fin.spr.annotations.LogExecutionTime;
import com.fin.spr.controllers.payload.LocationPayload;
import com.fin.spr.models.Category;
import com.fin.spr.models.Location;
import com.fin.spr.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
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
    @Value("${kudago.api.categories.url}")
    private String CATEGORIES_API_URL;

    @Value("${kudago.api.locations.url}")
    private String LOCATIONS_API_URL;

    private static final Logger logger = LoggerFactory.getLogger(KudaGoSerivce.class);

    @Value("${app.init.schedule-duration}")
    private Duration scheduleDuration;

    private final RestClient restClient;
    private InMemoryStorage<Category, Integer> categoryStorage;
    private final ExecutorService fixedThreadPool;
    private final ScheduledExecutorService scheduledThreadPool;
    private final LocationService locationService;


    public KudaGoSerivce(RestClient restClient,
                         InMemoryStorage<Category, Integer> categoryStorage,
                         LocationService locationService,
                         @Qualifier("fixedThreadPool") ExecutorService fixedThreadPool,
                         @Qualifier("scheduledThreadPool") ScheduledExecutorService scheduledThreadPool) {
        this.restClient = restClient;
        this.categoryStorage = categoryStorage;
        this.locationService = locationService;
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

    }
}
