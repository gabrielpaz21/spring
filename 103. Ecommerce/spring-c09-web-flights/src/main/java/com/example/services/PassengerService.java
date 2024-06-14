package com.example.services;

import com.example.entities.Passenger;

import java.util.List;

public interface PassengerService {
    List<Passenger> findAll();
    void save(Passenger passenger);
}
