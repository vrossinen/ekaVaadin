package com.example.application.services;

import com.example.application.data.Event;
import com.example.application.data.EventRepository;
import com.example.application.data.Location;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Event> findAll() {
        return repository.findAll(); // No need for Hibernate.initialize since the query fetches participants
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void save(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (event.getLocation() != null) {
            Optional<Event> existingEventOpt = repository.findByLocation(event.getLocation());
            if (existingEventOpt.isPresent()) {
                Event existingEvent = existingEventOpt.get();
                if (!existingEvent.getId().equals(event.getId())) {
                    throw new IllegalStateException("The location '" + event.getLocation().getName() + "' is already used by another event.");
                }
            }
        }
        repository.save(event);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void delete(Event event) {
        repository.delete(event);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Event findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Optional<Event> findByLocation(Location location) {
        return repository.findByLocation(location);
    }
}