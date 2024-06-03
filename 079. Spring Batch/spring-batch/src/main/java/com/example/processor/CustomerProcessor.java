package com.example.processor;

import com.example.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerProcessor.class);

    @Override
    public Customer process(@NonNull Customer customer) {
        LOG.info("Processing customer: {}", customer);
        customer.setFirstName(customer.getFirstName().toUpperCase());
        return customer;
    }
}
