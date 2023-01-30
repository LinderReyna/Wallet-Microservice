package com.jossemar.wallet.microservice.bus;

import com.jossemar.swap.microservice.event.Event;
import com.jossemar.wallet.microservice.model.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@RequiredArgsConstructor
@Slf4j
public class WalletProducer {
    @Value(value = "${kafka.topic.wallet.name}")
    private String topic;
    private final KafkaTemplate<String, Event<?>> kafkaTemplate;

    public void publish(Event<Wallet> event) {
        ListenableFuture<SendResult<String, Event<?>>> future = kafkaTemplate.send(this.topic, event);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, Event<?>> result) {
                log.info("Message {} has been sent ", event);
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Something went wrong with the balanceModel {} ", event);
            }
        });
    }
}
