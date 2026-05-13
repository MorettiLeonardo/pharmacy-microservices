package com.pharmacy.inventory.web.dto;

import com.pharmacy.inventory.domain.Product;
import java.time.LocalDate;

public record ProductResponse(Long id, String name, Integer stock, LocalDate expirationDate, Boolean controlled) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getStock(),
            product.getExpirationDate(),
            product.getControlled()
        );
    }
}
