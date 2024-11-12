package com.fin.spr.repository.jpa;

import com.fin.spr.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("SELECT l FROM Location l JOIN FETCH l.events WHERE l.id = :id")
    Optional<Location> findByIdWithEvents(Long id);
}
