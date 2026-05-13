package com.pharmacy.sales.api;

import com.pharmacy.sales.domain.Sale;

import java.time.LocalDateTime;

public record SaleResponse(
        Long id,
        Long productId,
        Integer quantity,
        LocalDateTime createdAt
) {
    public static SaleResponse from(Sale sale) {
        return new SaleResponse(
                sale.getId(),
                sale.getProductId(),
                sale.getQuantity(),
                sale.getCreatedAt()
        );
    }
}
