package com.example.propagation.service;

import com.example.propagation.model.EmployeeAddress;
import com.example.propagation.repository.EmployeeAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeAddressService {

    private final EmployeeAddressRepository repository;

    public EmployeeAddressService(EmployeeAddressRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insert(EmployeeAddress user) {
        repository.save(user);
    }


}
