package com.fin.spr.repository.jpa;

import com.fin.spr.models.Event;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
     List<Event> findAll(Specification<Event> specification);

     @Query("SELECT e FROM Event e JOIN FETCH e.location")
     List<Event> findAllWithLocations();
}
