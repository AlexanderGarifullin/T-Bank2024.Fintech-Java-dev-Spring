package com.fin.spr.config;

import com.fin.spr.models.Category;
import com.fin.spr.models.Location;
import com.fin.spr.storage.InMemoryStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The {@code StorageConfig} class provides configuration for in-memory storage
 * beans in the application. It defines the storage for {@link Category} and
 * {@link Location} entities.
 */
@Configuration
public class StorageConfig {

    /**
     * Creates and returns a new instance of {@link InMemoryStorage} for
     * managing {@link Category} entities.
     *
     * @return an {@link InMemoryStorage} instance for {@link Category} with
     *         {@link Integer} as the identifier type
     */
    @Bean
    public InMemoryStorage<Category, Integer> categoryStorage() {
        return new InMemoryStorage<>();
    }

    /**
     * Creates and returns a new instance of {@link InMemoryStorage} for
     * managing {@link Location} entities.
     *
     * @return an {@link InMemoryStorage} instance for {@link Location} with
     *         {@link String} as the identifier type
     */
    @Bean
    public InMemoryStorage<Location, String> locationStorage() {
        return new InMemoryStorage<>();
    }
}
