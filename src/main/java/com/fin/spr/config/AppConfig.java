package com.fin.spr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * The {@code AppConfig} class provides the configuration for the application.
 * It contains bean definitions that are used throughout the application.
 */
@Configuration
public class AppConfig {

    /**
     * Creates and returns a new instance of {@link RestClient}.
     *
     * @return a {@link RestClient} instance used for making HTTP requests
     */
    @Bean
    public RestClient restClient() {
        return  RestClient.create();
    }
}
