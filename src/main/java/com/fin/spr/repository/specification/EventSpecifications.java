package com.fin.spr.repository.specification;

import com.fin.spr.models.Event;
import com.fin.spr.models.Location;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public final class EventSpecifications {

    private static final String NAME_FIELD = "name";
    private static final String LOCATION_FIELD = "location";
    private static final String START_DAY_FIELD = "startDate";

    public static Specification<Event> buildSpecification(String name, Location location,
                                                        Instant fromDate, Instant toDate) {
        List<Specification<Event>> specifications = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            specifications.add((Specification<Event>) (event, query, cb) ->
                    cb.equal(event.get(NAME_FIELD), name));
        }

        if (location != null) {
            specifications.add((Specification<Event>) (event, query, cb) ->
                    cb.equal(event.get(LOCATION_FIELD), location));
        }

        if (fromDate != null && toDate != null) {
            specifications.add((Specification<Event>) (event, query, cb) ->
                    cb.between(event.get(START_DAY_FIELD), fromDate, toDate));
        } else if (fromDate != null) {
            specifications.add((Specification<Event>) (event, query, cb) ->
                    cb.greaterThanOrEqualTo(event.get(START_DAY_FIELD), fromDate));
        } else if (toDate != null) {
            specifications.add((Specification<Event>) (event, query, cb) ->
                    cb.lessThanOrEqualTo(event.get(START_DAY_FIELD), toDate));
        }

        return specifications.stream().reduce(Specification::and).orElse(null);
    }
}
