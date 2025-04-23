package com.example.application.services;

import com.example.application.data.Location;
import com.example.application.data.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    private final LocationRepository repository;

    public LocationService(LocationRepository repository) {
        this.repository = repository;
    }

    public List<Location> findAll() {
        return repository.findAll();
    }

    public void save(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        repository.save(location);
    }

    public void delete(Location location) {
        repository.delete(location);
    }

    public Location findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
