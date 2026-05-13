package com.pharmacy.inventory.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long productId, Integer availableStock, Integer requestedQuantity) {
        super(
            "Insufficient stock for product "
                + productId
                + ". Available="
                + availableStock
                + ", requested="
                + requestedQuantity
        );
    }
}
