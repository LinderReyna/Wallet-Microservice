package com.jossemar.wallet.microservice.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Coin {
    private String coinType;
    private BigDecimal availableAmount;
    private BigDecimal buyWithPEN;
    private BigDecimal saleWithPEN;
}
