package com.jossemar.wallet.microservice.bus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jossemar.swap.microservice.event.Event;
import com.jossemar.wallet.microservice.model.Swap;
import com.jossemar.wallet.microservice.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SwapConsumer {
    @Autowired
    private WalletService service;

    @KafkaListener(topics = "${kafka.topic.swap.name}", containerFactory = "kafkaListenerContainerFactory", groupId = "group_id")
    public void consumer(Event<Swap> event) {
        log.info("Message received : {} ", event);
        ObjectMapper mapper = new ObjectMapper();
        Swap swap = mapper.convertValue(event.getData(), new TypeReference<>() {});
        service.validBySwap(swap).subscribe();
    }
}
