package com.jossemar.wallet.microservice.controller;

import com.jossemar.wallet.microservice.api.WalletApi;
import com.jossemar.wallet.microservice.model.Wallet;
import com.jossemar.wallet.microservice.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/api/microservice/1.0.0")
@RestController
@Slf4j
public class WalletController implements WalletApi {
    @Autowired
    private WalletService service;

    @Override
    public Mono<ResponseEntity<Void>> delete(String id, ServerWebExchange exchange) {
        return service.deleteById(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Flux<Wallet>>> findAll(ServerWebExchange exchange) {
        return Mono.just(service.findAll())
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Wallet>> findById(String id, ServerWebExchange exchange) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Wallet>> save(Mono<Wallet> wallet, ServerWebExchange exchange) {
        return service.save(wallet)
                .map(ResponseEntity.status(HttpStatus.CREATED)::body);
    }

    @Override
    public Mono<ResponseEntity<Wallet>> update(String id, Mono<Wallet> wallet, ServerWebExchange exchange) {
        return service.update(wallet, id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Wallet>> findByCustomer(String customer, ServerWebExchange exchange) {
        return service.findByCustomer(customer)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
