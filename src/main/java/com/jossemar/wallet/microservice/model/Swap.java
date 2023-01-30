package com.jossemar.wallet.microservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class Swap {
    private String id;
    private String status;
    private String walletId;
    private String coinType;
    private BigDecimal amount;
    private String referenceId;
    private String transactionNumber;
    private String description;
    private String notification;
    @JsonIgnore
    private Instant createdAt;
    @JsonIgnore
    private Instant updatedAt;
}
