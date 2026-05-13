package com.pharmacy.sales.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSaleRequest(
        @NotNull Long productId,
        @NotNull @Positive Integer quantity
) {
}
