package com.fin.spr.config;

import com.fin.spr.services.observer.LoggingSubscriber;
import com.fin.spr.services.observer.MessagePublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObserverConfig {
    @Bean
    public LoggingSubscriber loggingSubscriber() {
        return new LoggingSubscriber();
    }

    @Bean
    public MessagePublisher messagePublisher(LoggingSubscriber loggingSubscriber) {
        MessagePublisher messagePublisher = new MessagePublisher();
        messagePublisher.attach(loggingSubscriber);
        return messagePublisher;
    }
}

