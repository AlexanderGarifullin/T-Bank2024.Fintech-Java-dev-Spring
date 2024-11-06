package com.fin.spr.services;

import com.fin.spr.annotations.LogExecutionTime;
import com.fin.spr.models.Category;
import com.fin.spr.models.Location;
import com.fin.spr.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.stereotype.Service;

/**
 * The {@code DataLoaderService} class is responsible for initializing and loading data
 * from the KudaGo API into in-memory storage for categories and locations.
 * <p>
 * This service is primarily designed to operate in the production environment, as
 * indicated by the {@link Profile} annotation with a value of "prod". It leverages
 * the {@link KudaGoSerivce} to load data from an external API.
 * </p>
 * <p>
 * The {@link LogExecutionTime} annotation is used to measure the execution time of
 * data loading, providing insights into the performance of this operation.
 * </p>
 * <p>
 * This service listens for the {@link ApplicationStartedEvent}, triggering the
 * {@link #initData()} method to load data when the application starts.
 * </p>
 *
 * @see Category
 * @see Location
 * @see InMemoryStorage
 * @see LogExecutionTime
 * @see KudaGoSerivce
 *
 * @version 1.0
 */
@Service
@LogExecutionTime
@Profile("prod")
public class DataLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(DataLoaderService.class);

    private final KudaGoSerivce kudaGoSerivce;

    /**
     * Constructs a new {@code DataLoaderService} instance.
     *
     * @param kudaGoSerivce the service used to retrieve data from the KudaGo API
     */
    @Autowired
    public DataLoaderService(KudaGoSerivce kudaGoSerivce) {
        this.kudaGoSerivce = kudaGoSerivce;
    }

    /**
     * Initializes data by invoking the {@link KudaGoSerivce#initData()} method.
     * <p>
     * This method is triggered by the {@link ApplicationStartedEvent} and is executed
     * automatically when the application starts. It retrieves data from the KudaGo API
     * and stores it in an in-memory storage for later use.
     * </p>
     */
    @EventListener(ApplicationStartedEvent.class)
    @LogExecutionTime
    public void initData() {
        logger.info("Starting data initialization...");
        kudaGoSerivce.initData();
        logger.info("Data initialization completed.");
    }
}
