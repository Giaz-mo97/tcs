package com.jiazhan.customermanagement.controllers;

import com.jiazhan.customermanagement.entities.Customer;
import com.jiazhan.customermanagement.entities.CustomerRequest;
import com.jiazhan.customermanagement.entities.CustomerResponse;
import com.jiazhan.customermanagement.services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
@Validated
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    /**
     * Create a new customer.
     */
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CustomerRequest request) {
        Customer created = service.create(request);
        CustomerResponse response = service.toResponse(created);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieve customer by UUID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable UUID id) {
        CustomerResponse response = service.toResponse(service.findById(id));
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve customer by name or email.
     * Provide either `?name=` or `?email=` as query parameter.
     */
    @GetMapping
    public ResponseEntity<CustomerResponse> getCustomerBy(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        Customer customer = (name != null)
                ? service.findByName(name)
                : service.findByEmail(email);
        return ResponseEntity.ok(service.toResponse(customer));
    }

    /**
     * Update an existing customer.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerRequest request) {
        Customer updated = service.update(id, request);
        return ResponseEntity.ok(service.toResponse(updated));
    }

    /**
     * Delete a customer by UUID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable UUID id) {
        service.delete(id);
    }
}

