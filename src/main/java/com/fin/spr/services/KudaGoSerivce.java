package com.fin.spr.services;

import com.fin.spr.annotations.LogExecutionTime;
import com.fin.spr.command.DataLoaderInvoker;
import com.fin.spr.models.Category;
import com.fin.spr.models.Location;
import com.fin.spr.storage.InMemoryStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
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
@Slf4j
public class KudaGoSerivce  {

    @Value("${app.init.schedule-duration}")
    private Duration scheduleDuration;

    private final ScheduledExecutorService scheduledThreadPool;
    private final DataLoaderInvoker dataLoaderInvoker;

    public KudaGoSerivce(@Qualifier("scheduledThreadPool") ScheduledExecutorService scheduledThreadPool,
                         DataLoaderInvoker dataLoaderInvoker) {
        this.scheduledThreadPool = scheduledThreadPool;
        this.dataLoaderInvoker = dataLoaderInvoker;
    }

    /**
     * Initiates the periodic data loading process, scheduling data updates based
     * on the specified {@code scheduleDuration}.
     * <p>
     * This method runs at fixed intervals defined by {@code scheduleDuration} and
     * triggers the parallel loading of categories
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
        dataLoaderInvoker.executeCommands();
    }
}
