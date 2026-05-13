package com.pharmacy.inventory.messaging;

import com.pharmacy.inventory.domain.Product;
import com.pharmacy.inventory.exception.InsufficientStockException;
import com.pharmacy.inventory.exception.ProductNotFoundException;
import com.pharmacy.inventory.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SaleCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(SaleCreatedConsumer.class);

    private final ProductService productService;

    public SaleCreatedConsumer(ProductService productService) {
        this.productService = productService;
    }

    @KafkaListener(topics = "${app.kafka.topics.sale-created:sale-created}")
    public void consume(SaleCreatedEvent event) {
        if (event == null) {
            log.warn("Received null sale-created event. Ignoring.");
            return;
        }
        log.info(
            "Received sale-created event: saleId={}, productId={}, quantity={}",
            event.saleId(),
            event.productId(),
            event.quantity()
        );

        try {
            Product updated = productService.decrementStock(event.productId(), event.quantity());
            log.info(
                "Stock updated successfully for productId={} after saleId={}. New stock={}",
                updated.getId(),
                event.saleId(),
                updated.getStock()
            );
        } catch (ProductNotFoundException | InsufficientStockException | IllegalArgumentException exception) {
            log.warn(
                "Could not apply sale-created event saleId={} for productId={}: {}. Event ignored.",
                event.saleId(),
                event.productId(),
                exception.getMessage()
            );
        }
    }
}
