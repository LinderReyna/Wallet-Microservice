package com.jossemar.wallet.microservice.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document
public class Wallet {
    @Id
    private String id;
    private String status;
    private String cardId;
    @Indexed(unique = true)
    private String customerId;
    private String description;
    private List<Coin> coin;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
