package com.jossemar.wallet.microservice.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Coin {
    private String coinType;
    private BigDecimal availableAmount;
}
