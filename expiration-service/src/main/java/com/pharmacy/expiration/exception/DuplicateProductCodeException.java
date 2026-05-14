package com.pharmacy.expiration.exception;

public class DuplicateProductCodeException extends RuntimeException {
    public DuplicateProductCodeException(String productCode) {
        super("Product code already exists: " + productCode);
    }
}
