package com.example.application.services;

import com.example.application.data.Organizer;
import com.example.application.data.OrganizerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizerService {
    private final OrganizerRepository repository;

    public OrganizerService(OrganizerRepository repository) {
        this.repository = repository;
    }

    public List<Organizer> findAll() {
        return repository.findAll();
    }

    public void save(Organizer organizer) {
        if (organizer == null) {
            throw new IllegalArgumentException("Organizer cannot be null");
        }
        repository.save(organizer);
    }

    public void delete(Organizer organizer) {
        repository.delete(organizer);
    }

    public Organizer findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
