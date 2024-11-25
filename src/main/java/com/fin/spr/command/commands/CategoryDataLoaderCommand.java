package com.fin.spr.command.commands;

import com.fin.spr.command.DataLoaderCommand;
import com.fin.spr.models.Category;
import com.fin.spr.storage.InMemoryStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@Slf4j
public class CategoryDataLoaderCommand implements DataLoaderCommand {
    private final RestClient restClient;

    @Value("${kudago.api.categories.url}")
    private String categoriesApiUrl;

    private final InMemoryStorage<Category, Integer> categoryStorage;

    public CategoryDataLoaderCommand(RestClient restClient,
                                     InMemoryStorage<Category, Integer> categoryStorage) {
        this.restClient = restClient;
        this.categoryStorage = categoryStorage;
    }

    @Override
    public void execute() {
        initCategories();
    }

    private void initCategories() {
        List<Category> categories = restClient.get()
                .uri(categoriesApiUrl)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        if (categories != null) {
            categories.forEach(category -> categoryStorage.create(category.getId(), category));
            log.info("Categories have been successfully initialized.");
        } else {
            log.warn("Received null categories.");
        }
    }
}
