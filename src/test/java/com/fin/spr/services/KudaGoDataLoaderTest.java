package com.fin.spr.services;

import com.fin.spr.models.Category;
import com.fin.spr.models.Location;
import com.fin.spr.storage.InMemoryStorage;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ExtendWith(MockitoExtension.class)
public class KudaGoDataLoaderTest {

    @Container
    private static GenericContainer<?> wiremockContainer = new GenericContainer<>("wiremock/wiremock:2.35.0")
            .withExposedPorts(8080);

    @Autowired
    private KudaGoDataLoader kudaGoDataLoader;

    @Autowired
    private InMemoryStorage<Category, Integer> categoryStorage;

    @Autowired
    private InMemoryStorage<Location, String> locationStorage;

    @DynamicPropertySource
    static void wiremockProperties(DynamicPropertyRegistry registry) {
        String wiremockUrl = String.format("http://localhost:%d", wiremockContainer.getFirstMappedPort());
        registry.add("categories.api.url", () -> wiremockUrl + "/public-api/v1.4/place-categories/");
        registry.add("locations.api.url", () -> wiremockUrl + "/public-api/v1.4/locations/");
    }

    @BeforeEach
    public void setUp() {
        WireMock.configureFor("localhost", wiremockContainer.getFirstMappedPort());
        WireMock.stubFor(get("/public-api/v1.4/place-categories/")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\": 1, \"slug\": \"category-1\", \"name\": \"Category 1\"}]")));

        WireMock.stubFor(get("/public-api/v1.4/locations/")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"slug\": \"location-1\", \"name\": \"Location 1\"}]")));
    }

    @Test
    public void testInitData() {
        kudaGoDataLoader.initData();

        assertThat(categoryStorage.getAll())
                .hasSize(1)
                .contains(new Category(1, "category-1", "Category 1"));

        assertThat(locationStorage.getAll())
                .hasSize(1)
                .contains(new Location("location-1", "Location 1"));
    }
}