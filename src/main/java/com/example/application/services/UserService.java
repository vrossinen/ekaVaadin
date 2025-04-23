package com.example.application.services;

import com.example.application.data.User;
import com.example.application.data.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public void save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        repository.save(user);
    }

    public void delete(User user) {
        repository.delete(user);
    }

    public User findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }
}
