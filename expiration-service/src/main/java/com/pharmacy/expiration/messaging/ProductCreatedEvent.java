package com.pharmacy.expiration.messaging;

import java.time.LocalDate;

public record ProductCreatedEvent(
        Long productId,
        String name,
        LocalDate expirationDate
) {
}
