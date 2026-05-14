package com.pharmacy.expiration.messaging;

import com.pharmacy.expiration.service.ExpirationCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductCreatedConsumer.class);

    private final ExpirationCheckService expirationCheckService;

    public ProductCreatedConsumer(ExpirationCheckService expirationCheckService) {
        this.expirationCheckService = expirationCheckService;
    }

    @KafkaListener(topics = "${app.kafka.topics.product-created:product-created}")
    public void consume(ProductCreatedEvent event) {
        if (event == null || event.productId() == null) {
            log.warn("Received invalid product-created event. Ignoring.");
            return;
        }

        expirationCheckService.upsertFromInventory(event.productId(), event.name(), event.expirationDate());
    }
}
