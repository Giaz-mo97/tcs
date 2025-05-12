package com.jiazhan.customermanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiazhan.customermanagement.controllers.CustomerController;
import com.jiazhan.customermanagement.entities.Customer;
import com.jiazhan.customermanagement.entities.CustomerRequest;
import com.jiazhan.customermanagement.entities.CustomerResponse;
import com.jiazhan.customermanagement.services.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService service;

    @Test
    void createCustomer_InvalidEmail_ShouldReturnBadRequest() throws Exception {
        // Prepare request with invalid email format
        CustomerRequest invalidEmailRequest = new CustomerRequest(
                "John Doe",
                "not-a-valid-email",
                new BigDecimal("1000"),
                OffsetDateTime.now()
        );

        // Perform POST and expect 400 Bad Request
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='email')]").exists());
    }

    @Test
    void createCustomer_ValidEmail_ShouldReturnCreated() throws Exception {
        // Setup service to return a dummy customer
        Customer dummy = new Customer();
        dummy.setId(UUID.randomUUID());
        dummy.setName("John Doe");
        dummy.setEmail("john.doe@example.com");
        dummy.setAnnualSpend(new BigDecimal("1500"));
        dummy.setLastPurchaseDate(OffsetDateTime.now().minus(2, ChronoUnit.MONTHS));
        when(service.create(any(CustomerRequest.class))).thenReturn(dummy);
        when(service.toResponse(any(Customer.class))).thenReturn(
                new CustomerResponse(dummy.getId(), dummy.getName(), dummy.getEmail(), dummy.getAnnualSpend(), dummy.getLastPurchaseDate(), "Gold")
        );

        CustomerRequest validRequest = new CustomerRequest(
                "John Doe",
                "john.doe@example.com",
                new BigDecimal("1500"),
                OffsetDateTime.now().minus(2, ChronoUnit.MONTHS)
        );

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());
    }
}
