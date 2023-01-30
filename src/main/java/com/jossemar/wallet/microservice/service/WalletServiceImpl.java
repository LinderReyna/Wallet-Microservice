package com.jossemar.wallet.microservice.service;

import com.jossemar.swap.microservice.event.Event;
import com.jossemar.wallet.microservice.bus.WalletProducer;
import com.jossemar.swap.microservice.event.EventType;
import com.jossemar.wallet.microservice.exception.InvalidDataException;
import com.jossemar.wallet.microservice.mapper.WalletMapper;
import com.jossemar.wallet.microservice.model.Coin;
import com.jossemar.wallet.microservice.model.Swap;
import com.jossemar.wallet.microservice.model.Wallet;
import com.jossemar.wallet.microservice.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@Transactional
public class WalletServiceImpl implements WalletService {
    @Autowired
    private WalletRepository repository;
    @Autowired
    private WalletMapper mapper;
    @Autowired
    private WalletProducer producer;

    @Override
    public Mono<Wallet> save(Mono<Wallet> wallet) {
        return wallet.filter(this::validation)
                .map(mapper::toDocument)
                .flatMap(repository::save)
                .map(mapper::toModel);
    }

    @Override
    public Flux<Wallet> findAll() {
        return repository.findAll()
                .map(mapper::toModel);
    }

    @Override
    public Mono<Wallet> findById(String id) {
        return repository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Wallet> update(Mono<Wallet> wallet, String id) {
        return save(findById(id)
                .flatMap(c -> wallet.doOnNext(x -> x.setCreatedAt(c.getCreatedAt())))
                .doOnNext(e -> e.setId(id)));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return findById(id)
                .map(mapper::toDocument)
                .flatMap(repository::delete);
    }

    @Override
    public Flux<Wallet> findAllByCustomerId(String customerId) {
        return repository.findAllByCustomerId(customerId)
                .map(mapper::toModel);
    }

    private Boolean validation(Wallet wallet) {
        long distinctCoinType = wallet.getCoin().stream().map(Coin::getCoinType).distinct().count();
        if (distinctCoinType != wallet.getCoin().size()) {
            throw new InvalidDataException("Coin type can not be repeated");
        }
        return true;
    }

    /*@Override
    public Mono<Void> validBySwap(Swap swap) {
        return findById(swap.getWalletId())
                //validar que exista la billetera
                .switchIfEmpty(Mono.defer(() -> {
                    kafkaTemplate.sendMessage(Event.<Wallet>builder()
                                    .id(swap.getId())
                                    .type(EventType.NOT_FOUND)
                            .build());
                    return Mono.empty();
                }))
                .flatMap(w -> {
                    //validar que exista el tipo de moneda
                    if (w.getCoin().stream().noneMatch(c -> c.getCoinType().getValue().equals(swap.getCoinType()))) {
                        kafkaTemplate.sendMessage(Event.<Wallet>builder()
                                .id(swap.getId())
                                .type(EventType.INVALID_COIN)
                                .build());
                        return Mono.empty();
                    }
                    else {
                        w.getCoin().forEach(c -> {
                            if (c.getCoinType().getValue().equals(swap.getCoinType())) {
                                //validar que haya saldo suficiente
                                if (c.getAvailableAmount().compareTo(swap.getAmount()) < 0) {
                                    kafkaTemplate.sendMessage(Event.<Wallet>builder()
                                            .id(swap.getId())
                                            .type(EventType.LOW_BALANCE)
                                            .build());
                                }
                                else {
                                    //Restar saldo de la billetera
                                    c.setAvailableAmount(c.getAvailableAmount().subtract(swap.getAmount()));
                                }
                            }
                        });
                        return Mono.just(w);
                    }
                })
                .flatMap(w -> {
                    //validar que exista la billetera de destino
                    return findById(swap.getReferenceId())
                            .switchIfEmpty(Mono.defer(() -> {
                                kafkaTemplate.sendMessage(Event.<Wallet>builder()
                                        .id(swap.getId())
                                        .type(EventType.INVALID_DESTINATION)
                                        .build());
                                return Mono.empty();
                            }))
                            .then(Mono.just(w));
                })
                //actulizar billetera y enviar exito
                .flatMap(w -> update(Mono.just(w), swap.getId())
                        .doOnNext(v -> kafkaTemplate.sendMessage(Event.<Wallet>builder()
                                .id(swap.getId())
                                .type(EventType.VALID)
                                .build())))
                .then();
    }*/
    @Override
    public Mono<Void> validBySwap(Swap swap) {
        AtomicBoolean isTransactionValid = new AtomicBoolean(true);
        AtomicReference<EventType> eventType = new AtomicReference<>(EventType.VALID);
        return findById(swap.getWalletId())
                .switchIfEmpty(Mono.defer(() -> {
                    isTransactionValid.set(false);
                    eventType.set(EventType.NOT_FOUND);
                    return Mono.empty();
                }))
                .flatMap(wallet -> {
                    Coin coin = findCoinByType(wallet, swap.getCoinType());
                    if (coin == null) {
                        isTransactionValid.set(false);
                        eventType.set(EventType.INVALID_COIN);
                        return Mono.empty();
                    } else if (coin.getAvailableAmount().add(swap.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
                        isTransactionValid.set(false);
                        eventType.set(EventType.LOW_BALANCE);
                        return Mono.empty();
                    } else {
                        coin.setAvailableAmount(coin.getAvailableAmount().add(swap.getAmount()));
                        sendEventMessage(swap.getId(), eventType.get());
                        return update(Mono.just(wallet), swap.getWalletId());
                    }
                })
                .flatMap(wallet -> findById(swap.getReferenceId())
                        .switchIfEmpty(Mono.defer(() -> {
                            isTransactionValid.set(false);
                            eventType.set(EventType.INVALID_DESTINATION);
                            return Mono.empty();
                        }))
                )
                .thenEmpty(t -> {
                    if (!isTransactionValid.get()) {
                        sendEventMessage(swap.getId(), eventType.get());
                    }
                });
    }

    private Coin findCoinByType(Wallet wallet, String coinType) {
        return wallet.getCoin().stream()
                .filter(c -> c.getCoinType().getValue().equals(coinType))
                .findFirst()
                .orElse(null);
    }

    private void sendEventMessage(String id, EventType eventType) {
        Event<Wallet> event = new Event<>();
        event.setId(id);
        event.setType(eventType);
        producer.publish(event);
    }

}
