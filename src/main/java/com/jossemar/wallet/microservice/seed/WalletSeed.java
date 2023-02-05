package com.jossemar.wallet.microservice.seed;

import com.jossemar.wallet.microservice.domain.Coin;
import com.jossemar.wallet.microservice.domain.Wallet;
import com.jossemar.wallet.microservice.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;

@Component
public class WalletSeed {
    @Autowired
    private WalletRepository repository;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        var coins = new ArrayList<Coin>();
        coins.add(Coin.builder().coinType("SOLES").availableAmount(BigDecimal.ZERO).build());
        coins.add(Coin.builder().coinType("BOOTCOIN").availableAmount(BigDecimal.ZERO).build());
        var wallet = Wallet.builder()
                .id("63859d33434879511128cf355")
                .status("ACTIVO")
                .customerId("63859d33434879511128cf355")
                .description("Mi Banco")
                .coin(coins)
                .createdAt(Instant.now())
                .build();
        try {
            repository.findById(wallet.getId())
                    .switchIfEmpty(repository.save(wallet)).subscribe();
        } catch (Exception ignored) { }
    }
}
