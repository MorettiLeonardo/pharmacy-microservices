package com.pharmacy.inventory.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

public record CreateProductRequest(
    @NotBlank String name,
    @NotNull @PositiveOrZero Integer stock,
    @NotNull LocalDate expirationDate,
    @NotNull Boolean controlled
) {
}
