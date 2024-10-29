package com.fin.spr.services;

import com.fin.spr.annotations.LogExecutionTime;
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
    private InMemoryStorage<Location, String> locationStorage;
    private final ExecutorService fixedThreadPool;
    private final ScheduledExecutorService scheduledThreadPool;


    public KudaGoSerivce(RestClient restClient,
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
        List<Category> categories = null;
        try {
            var v  = restClient.get()
                    .uri(CATEGORIES_API_URL)
                    .retrieve();
            categories = v.body(new ParameterizedTypeReference<>() {});
        }
        catch (Exception e) {
            logger.info("ERROR LOAD CATEGORIES ");
            logger.error(e.getMessage());
        }
        logger.info("Size categories = " + categories.size());
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
        List<Location> locations = null;
        try {
            var v  = restClient.get()
                    .uri(LOCATIONS_API_URL)
                    .retrieve();
            locations = v.body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            logger.info("ERROR loadLocations ");
            logger.error(e.getMessage());
        }

        logger.info("Size locations = " + locations.size());
        if (locations != null) {
            locations.forEach(location -> locationStorage.create(location.getSlug(), location));
            logger.info("Locations have been successfully initialized.");
        } else {
            logger.warn("Received null locations.");
        }
    }
}
