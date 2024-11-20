package com.fin.spr.controllers;

import com.fin.spr.BaseIntegrationTest;
import com.fin.spr.controllers.payload.LocationPayload;
import com.fin.spr.repository.jpa.LocationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class LocationControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    void testCreateAndRetrieveLocation() throws Exception {
        LocationPayload payload = new LocationPayload("slug1", "Test Location");
    }
}