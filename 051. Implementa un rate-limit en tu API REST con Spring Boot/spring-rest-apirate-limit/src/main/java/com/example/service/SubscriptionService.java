package com.example.service;

import com.example.model.Subscription;
import com.example.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    private final SubscriptionRepository repo;

    public SubscriptionService(SubscriptionRepository repo) {
        this.repo = repo;
    }

    public Subscription findByUserId(Long id) {
        return this.repo.findByUserId(id);
    }

    public Subscription findByUserName(String username) {
        return this.repo.findByUserUsername(username);
    }

}
