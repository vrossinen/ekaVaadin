package com.example.application.services;

import com.example.application.data.Participant;
import com.example.application.data.ParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantService {
    private final ParticipantRepository repository;

    public ParticipantService(ParticipantRepository repository) {
        this.repository = repository;
    }

    public List<Participant> findAll() {
        return repository.findAll();
    }

    public void save(Participant participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Participant cannot be null");
        }
        repository.save(participant);
    }

    public void delete(Participant participant) {
        repository.delete(participant);
    }

    public Participant findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
