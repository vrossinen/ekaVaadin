package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.participants WHERE e.location = :location")
    Optional<Event> findByLocation(@Param("location") Location location);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.participants")
    List<Event> findAll();
}
