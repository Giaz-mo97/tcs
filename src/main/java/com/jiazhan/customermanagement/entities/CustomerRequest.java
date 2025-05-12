package com.jiazhan.customermanagement.entities;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {
    @NotBlank private String name;
    @NotBlank @Email private String email;
    private BigDecimal annualSpend;
    private OffsetDateTime lastPurchaseDate;
}
