package com.pharmacy.sales.messaging;

import com.pharmacy.sales.domain.Sale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SaleCreatedProducer {

    private static final Logger log = LoggerFactory.getLogger(SaleCreatedProducer.class);

    private final KafkaTemplate<String, SaleCreatedEvent> kafkaTemplate;
    private final String saleCreatedTopic;

    public SaleCreatedProducer(
            KafkaTemplate<String, SaleCreatedEvent> kafkaTemplate,
            @Value("${app.kafka.topics.sale-created:sale-created}") String saleCreatedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.saleCreatedTopic = saleCreatedTopic;
    }

    public void publish(Sale sale) {
        SaleCreatedEvent event = new SaleCreatedEvent(sale.getId(), sale.getProductId(), sale.getQuantity());
        kafkaTemplate.send(saleCreatedTopic, sale.getId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Published sale-created event for saleId={}", sale.getId());
                    } else {
                        log.error("Failed to publish sale-created event for saleId={}", sale.getId(), ex);
                    }
                });
    }
}
