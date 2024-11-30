package com.fin.spr.controllers;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomMetricsController {

    private final MeterRegistry meterRegistry;

    public CustomMetricsController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/custom-metric")
    public String incrementCustomMetric() {
        meterRegistry.counter("custom_metric_total", "type", "custom").increment();
        return "Custom metric incremented!";
    }
}
