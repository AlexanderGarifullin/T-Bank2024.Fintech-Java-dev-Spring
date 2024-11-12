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

    public static Specification<Event> buildSpecification(String name, Location location,
                                                        Instant fromDate, Instant toDate) {
        List<Specification<Event>> specifications = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            specifications.add((Specification<Event>) (event, query, cb) ->
                    cb.equal(event.get("name"), name));
        }

        if (location != null) {
            specifications.add((Specification<Event>) (event, query, cb) ->
                    cb.equal(event.get("location"), location));
        }

        if (fromDate != null && toDate != null) {
            specifications.add((Specification<Event>) (event, query, cb) ->
                    cb.between(event.get("startDate"), fromDate, toDate));
        } else if (fromDate != null) {
            specifications.add((Specification<Event>) (event, query, cb) ->
                    cb.greaterThanOrEqualTo(event.get("startDate"), fromDate));
        } else if (toDate != null) {
            specifications.add((Specification<Event>) (event, query, cb) ->
                    cb.lessThanOrEqualTo(event.get("startDate"), toDate));
        }

        return specifications.stream().reduce(Specification::and).orElse(null);
    }
}
