package com.jiazhan.customermanagement;

import com.jiazhan.customermanagement.entities.Customer;
import com.jiazhan.customermanagement.entities.CustomerRequest;
import com.jiazhan.customermanagement.entities.CustomerResponse;
import com.jiazhan.customermanagement.repositories.CustomerRepository;
import com.jiazhan.customermanagement.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    private Customer sample;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sample = new Customer();
        sample.setId(sampleId);
        sample.setName("Alice");
        sample.setEmail("alice@example.com");
        sample.setAnnualSpend(new BigDecimal("1500"));
        sample.setLastPurchaseDate(OffsetDateTime.now().minusMonths(3));
    }

    @Test
    void create_ShouldSaveAndReturnCustomer() {
        when(repository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerRequest req = new CustomerRequest(
                "Bob",
                "bob@example.com",
                new BigDecimal("2000"),
                OffsetDateTime.now().minusMonths(2)
        );

        Customer created = service.create(req);

        assertEquals("Bob", created.getName());
        assertEquals("bob@example.com", created.getEmail());
        assertEquals(new BigDecimal("2000"), created.getAnnualSpend());
        verify(repository).save(created);
    }

    @Test
    void findById_WhenExists_ShouldReturnCustomer() {
        when(repository.findById(sampleId)).thenReturn(Optional.of(sample));

        Customer found = service.findById(sampleId);
        assertSame(sample, found);
    }

    @Test
    void findById_WhenNotFound_ShouldThrow() {
        when(repository.findById(sampleId)).thenReturn(Optional.empty());
        assertThrows(CustomerService.ResourceNotFoundException.class,
                () -> service.findById(sampleId));
    }

    @Test
    void findByName_WhenExists_ShouldReturnCustomer() {
        when(repository.findByName("Alice")).thenReturn(Optional.of(sample));
        Customer found = service.findByName("Alice");
        assertSame(sample, found);
    }

    @Test
    void findByEmail_WhenExists_ShouldReturnCustomer() {
        when(repository.findByEmail("alice@example.com")).thenReturn(Optional.of(sample));
        Customer found = service.findByEmail("alice@example.com");
        assertSame(sample, found);
    }

    @Test
    void update_ShouldModifyAndSave() {
        when(repository.findById(sampleId)).thenReturn(Optional.of(sample));
        when(repository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerRequest req = new CustomerRequest(
                "Alice Updated",
                "alice.new@example.com",
                new BigDecimal("5000"),
                OffsetDateTime.now().minusMonths(1)
        );

        Customer updated = service.update(sampleId, req);

        assertEquals("Alice Updated", updated.getName());
        assertEquals("alice.new@example.com", updated.getEmail());
        assertEquals(new BigDecimal("5000"), updated.getAnnualSpend());
        verify(repository).save(updated);
    }

    @Test
    void delete_ShouldRemoveCustomer() {
        when(repository.findById(sampleId)).thenReturn(Optional.of(sample));

        service.delete(sampleId);

        verify(repository).delete(sample);
    }

    @Test
    void toResponse_ShouldComputeCorrectTier() {
        // Platinum: spend>=10000 and within 6 months
        sample.setAnnualSpend(new BigDecimal("12000"));
        sample.setLastPurchaseDate(OffsetDateTime.now().minusMonths(5));
        CustomerResponse resp1 = service.toResponse(sample);
        assertEquals("Platinum", resp1.getTier());

        // Gold: spend>=1000 and within 12 months
        sample.setAnnualSpend(new BigDecimal("2000"));
        sample.setLastPurchaseDate(OffsetDateTime.now().minusMonths(11));
        CustomerResponse resp2 = service.toResponse(sample);
        assertEquals("Gold", resp2.getTier());

        // Silver: all other cases
        sample.setAnnualSpend(new BigDecimal("500"));
        sample.setLastPurchaseDate(OffsetDateTime.now().minusMonths(2));
        CustomerResponse resp3 = service.toResponse(sample);
        assertEquals("Silver", resp3.getTier());

        // Silver when null spend
        sample.setAnnualSpend(null);
        sample.setLastPurchaseDate(null);
        CustomerResponse resp4 = service.toResponse(sample);
        assertEquals("Silver", resp4.getTier());
    }
}

