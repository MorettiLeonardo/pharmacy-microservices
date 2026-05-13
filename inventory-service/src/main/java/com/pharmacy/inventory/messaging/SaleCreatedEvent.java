package com.pharmacy.inventory.messaging;

public record SaleCreatedEvent(String saleId, Long productId, Integer quantity) {
}
