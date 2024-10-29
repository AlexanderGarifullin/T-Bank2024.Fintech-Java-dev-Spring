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
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
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
 * The {@code DataLoaderService} class is responsible for initializing and loading data
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
 * @see Category
 * @see Location
 * @see InMemoryStorage
 * @see LogExecutionTime
 *
 * @author Alexander Garifullin
 * @version 1.0
 */
@Service
@LogExecutionTime
@Profile("prod")
public class DataLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(DataLoaderService.class);


    private final KudaGoSerivce kudaGoSerivce;


    @Autowired
    public DataLoaderService(KudaGoSerivce kudaGoSerivce) {
        this.kudaGoSerivce = kudaGoSerivce;
    }

    @EventListener(ApplicationStartedEvent.class)
    @LogExecutionTime
    public void initData() {
        kudaGoSerivce.initData();
    }
}
