package com.jiazhan.customermanagement.services;

import com.jiazhan.customermanagement.entities.Customer;
import com.jiazhan.customermanagement.entities.CustomerRequest;
import com.jiazhan.customermanagement.entities.CustomerResponse;
import com.jiazhan.customermanagement.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional

public class CustomerService {
    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    /**
     * Create a new customer from the request.
     */
    public Customer create(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setAnnualSpend(request.getAnnualSpend());
        customer.setLastPurchaseDate(request.getLastPurchaseDate());
        return repository.save(customer);
    }

    /**
     * Find customer by UUID or throw if not found.
     */
    public Customer findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    /**
     * Find customer by name or throw if not found.
     */
    public Customer findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with name: " + name));
    }

    /**
     * Find customer by email or throw if not found.
     */
    public Customer findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));
    }

    /**
     * Update an existing customer with new data.
     */
    public Customer update(UUID id, CustomerRequest request) {
        Customer existing = findById(id);
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setAnnualSpend(request.getAnnualSpend());
        existing.setLastPurchaseDate(request.getLastPurchaseDate());
        return repository.save(existing);
    }

    /**
     * Delete a customer by id.
     */
    public void delete(UUID id) {
        Customer existing = findById(id);
        repository.delete(existing);
    }

    /**
     * Convert entity to response DTO, including computed tier.
     */
    public CustomerResponse toResponse(Customer customer) {
        String tier = calculateTier(
                customer.getAnnualSpend(),
                customer.getLastPurchaseDate()
        );
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getAnnualSpend(),
                customer.getLastPurchaseDate(),
                tier
        );
    }

    /**
     * Determine customer tier based on spend and recency.
     */
    private String calculateTier(BigDecimal spend, OffsetDateTime lastPurchaseDate) {
        if (spend == null) {
            return "Silver";
        }
        long monthsSince = lastPurchaseDate == null
                ? Long.MAX_VALUE
                : ChronoUnit.MONTHS.between(lastPurchaseDate, OffsetDateTime.now());

        if (spend.compareTo(new BigDecimal("10000")) >= 0 && monthsSince <= 6) {
            return "Platinum";
        }
        if (spend.compareTo(new BigDecimal("1000")) >= 0 && monthsSince <= 12) {
            return "Gold";
        }
        return "Silver";
    }

    /**
     * Custom exception for not found resources.
     */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
