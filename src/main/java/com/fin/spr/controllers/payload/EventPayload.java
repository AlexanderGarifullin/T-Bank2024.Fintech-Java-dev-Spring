package com.fin.spr.controllers.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record EventPayload  (

        @NotBlank(message = "{event.request.name.is_blank}")
        String name,

        @NotNull(message = "{event.request.startDate.is_null}")
        Instant startDate,

        String price,

        @NotNull(message = "{event.request.free.is_null}")
        boolean free,

        @NotNull(message = "{event.request.locationId.is_null}")
        Long locationId
) {
}
