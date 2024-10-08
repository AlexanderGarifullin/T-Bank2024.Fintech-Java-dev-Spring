package com.fin.spr.services;

import com.fin.spr.annotations.LogExecutionTime;
import com.fin.spr.models.Category;
import com.fin.spr.models.Location;
import com.fin.spr.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * The {@code KudaGoDataLoader} class is responsible for initializing data
 * from the KudaGo API into in-memory storage for categories and locations.
 *
 * <p>
 * This class uses the {@link RestClient} to make HTTP requests to the KudaGo API
 * and retrieves categories and locations. The retrieved data is stored in
 * {@link InMemoryStorage} for further use in the application.
 * </p>
 *
 * <p>
 * The initialization process is triggered when the application is ready,
 * and the execution time of the initialization method is logged using
 * the {@link LogExecutionTime} annotation.
 * </p>
 *
 * <p>
 * The class is annotated with {@code @Service}, allowing it to be detected
 * and managed by Spring's dependency injection framework.
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

    private static final Logger logger = LoggerFactory.getLogger(KudaGoDataLoader.class);

    private final RestClient restClient;

    private final InMemoryStorage<Category, Integer> categoryStorage;

    private final InMemoryStorage<Location, String> locationStorage;

    private final String categoriesApiUrl;
    private final String locationsApiUrl;

    /**
     * Constructs a new {@code KudaGoDataLoader} instance.
     *
     * @param restClient           The {@link RestClient} used to make HTTP requests to the KudaGo API.
     * @param categoryStorage      The {@link InMemoryStorage} for storing categories.
     * @param locationStorage     The {@link InMemoryStorage} for storing locations.
     * @param categoriesApiUrl The URL of the KudaGo API endpoint for categories.
     * @param locationsApiUrl The URL of the KudaGo API endpoint for locations.
     */
    @Autowired
    public KudaGoDataLoader(RestClient restClient, InMemoryStorage<Category, Integer> categoryStorage,
                            InMemoryStorage<Location, String> locationStorage,
                            @Value("${categories.api.url}") String categoriesApiUrl,
                            @Value("${locations.api.url}") String locationsApiUrl) {
        this.restClient = restClient;
        this.categoryStorage = categoryStorage;
        this.locationStorage = locationStorage;
        this.categoriesApiUrl = categoriesApiUrl;
        this.locationsApiUrl = locationsApiUrl;
    }

    /**
     * Initializes data from the KudaGo API and stores it in memory.
     * This method is invoked when the application is ready, indicated
     * by the {@link ApplicationReadyEvent}.
     *
     * <p>
     * The method retrieves categories and locations from their respective
     * API endpoints and stores them in the in-memory storage.
     * Log messages indicate the progress and completion of the initialization.
     * </p>
     */
    @EventListener(ApplicationReadyEvent.class)
    @LogExecutionTime
    public void initData() {
        logger.info("Starting data initialization from the KudaGo API...");

        List<Category> categories = restClient.get()
                .uri(categoriesApiUrl)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (categories != null) {
            categories.forEach(category -> categoryStorage.create(category.getId(), category));
            logger.info("Categories have been successfully initialized.");
        } else {
            logger.warn("Received null categories.");
        }

        List<Location> locations = restClient.get()
                .uri(locationsApiUrl)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (locations != null) {
            locations.forEach(location -> locationStorage.create(location.getSlug(), location));
            logger.info("Locations have been successfully initialized.");
        } else {
            logger.warn("Received null locations.");
        }

        logger.info("Data initialization completed.");
    }
}
