package com.pharmacy.inventory.messaging;

import com.pharmacy.inventory.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductCreatedProducer {

    private static final Logger log = LoggerFactory.getLogger(ProductCreatedProducer.class);

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final String productCreatedTopic;

    public ProductCreatedProducer(
            KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate,
            @Value("${app.kafka.topics.product-created:product-created}") String productCreatedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.productCreatedTopic = productCreatedTopic;
    }

    public void publish(Product product) {
        ProductCreatedEvent event = new ProductCreatedEvent(
                product.getId(),
                product.getName(),
                product.getExpirationDate()
        );

        kafkaTemplate.send(productCreatedTopic, product.getId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Published product-created event for productId={}", product.getId());
                    } else {
                        log.error("Failed to publish product-created event for productId={}", product.getId(), ex);
                    }
                });
    }
}
