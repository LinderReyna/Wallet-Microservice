package com.jossemar.wallet.microservice.repository;

import com.jossemar.wallet.microservice.domain.Wallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface WalletRepository extends ReactiveMongoRepository<Wallet, String> {
    Flux<Wallet> findAllByCustomerId(String customerId);
}
