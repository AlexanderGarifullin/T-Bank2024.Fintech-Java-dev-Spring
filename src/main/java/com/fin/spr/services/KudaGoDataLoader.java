package com.fin.spr.services;

import com.fin.spr.annotations.LogExecutionTime;
import com.fin.spr.models.Category;
import com.fin.spr.models.Location;
import com.fin.spr.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * The {@code KudaGoDataLoader} class is responsible for initializing and loading data
 * from the KudaGo API into in-memory storage for categories and locations.
 * <p>
 * This service uses a scheduled task to periodically load and store data from the
 * KudaGo API using parallel processing.
 * </p>
 * <p>
 * The {@link RestClient} is used to retrieve data, which is then stored in instances
 * of {@link InMemoryStorage} for later use.
 * </p>
 *
 * @see com.fin.spr.models.Category
 * @see com.fin.spr.models.Location
 * @see com.fin.spr.storage.InMemoryStorage
 * @see com.fin.spr.annotations.LogExecutionTime
 *
 * @author Alexander Garifullin
 * @version 1.0
 */
@Service
@LogExecutionTime
public class KudaGoDataLoader {

    private static final String CATEGORIES_API_URL = "https://kudago.com/public-api/v1.4/place-categories/";
    private static final String LOCATIONS_API_URL = "https://kudago.com/public-api/v1.4/locations/";
    private static final Logger logger = LoggerFactory.getLogger(KudaGoDataLoader.class);
    @Value("${app.init.schedule-duration}")
    private Duration scheduleDuration;

    private final RestClient restClient;
    private InMemoryStorage<Category, Integer> categoryStorage;
    private InMemoryStorage<Location, String> locationStorage;
    private final ExecutorService fixedThreadPool;
    private final ScheduledExecutorService scheduledThreadPool;

    /**
     * Constructs a new {@code KudaGoDataLoader} instance with dependencies injected by Spring.
     *
     * @param restClient         the REST client for making HTTP requests to the KudaGo API
     * @param categoryStorage    the in-memory storage for storing {@link Category} objects
     * @param locationStorage    the in-memory storage for storing {@link Location} objects
     * @param fixedThreadPool    the executor service for handling parallel data loading tasks
     * @param scheduledThreadPool the scheduled executor service for scheduling periodic data initialization
     */
    @Autowired
    public KudaGoDataLoader(
            RestClient restClient,
            InMemoryStorage<Category, Integer> categoryStorage,
            InMemoryStorage<Location, String> locationStorage,
            @Qualifier("fixedThreadPool") ExecutorService fixedThreadPool,
            @Qualifier("scheduledThreadPool") ScheduledExecutorService scheduledThreadPool) {
        this.restClient = restClient;
        this.categoryStorage = categoryStorage;
        this.locationStorage = locationStorage;
        this.fixedThreadPool = fixedThreadPool;
        this.scheduledThreadPool = scheduledThreadPool;
    }

    /**
     * Initializes data loading by scheduling the {@link #parallelInitData} method to run at fixed intervals.
     * <p>
     * This method is triggered automatically when the application starts.
     * </p>
     */
    @EventListener(ApplicationStartedEvent.class)
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
    @LogExecutionTime
    public void parallelInitData() {
        try {
            fixedThreadPool.invokeAll(List.of(
                    () -> { loadCategories(); return null; },
                    () -> { loadLocations(); return null; }
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

    /**
     * Loads locations from the KudaGo API and stores them in {@link InMemoryStorage}.
     * <p>
     * If no data is returned, a warning message is logged.
     * </p>
     */
    private void loadLocations() {
        List<Location> locations = restClient.get()
                .uri(LOCATIONS_API_URL)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (locations != null) {
            locations.forEach(location -> locationStorage.create(location.getSlug(), location));
            logger.info("Locations have been successfully initialized.");
        } else {
            logger.warn("Received null locations.");
        }
    }
}
