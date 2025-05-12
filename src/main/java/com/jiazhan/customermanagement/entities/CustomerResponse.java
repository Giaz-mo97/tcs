package com.jiazhan.customermanagement.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CustomerResponse {
    private UUID id;
    private String name;
    private String email;
    private BigDecimal annualSpend;
    private OffsetDateTime lastPurchaseDate;
    private String tier;
}
