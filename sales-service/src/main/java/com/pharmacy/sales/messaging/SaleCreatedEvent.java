package com.pharmacy.sales.messaging;

public record SaleCreatedEvent(
        Long saleId,
        Long productId,
        Integer quantity
) {
}
