package com.pharmacy.expiration.api;

import com.pharmacy.expiration.product.ProductExpiration;
import com.pharmacy.expiration.service.ExpirationCheckService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expirations/products")
public class ProductExpirationController {

    private final ExpirationCheckService expirationCheckService;

    public ProductExpirationController(ExpirationCheckService expirationCheckService) {
        this.expirationCheckService = expirationCheckService;
    }

    @GetMapping
    public List<ProductExpiration> getAllProducts() {
        return expirationCheckService.findAllProducts();
    }

    @GetMapping("/expired")
    public List<ProductExpiration> getExpiredProducts() {
        return expirationCheckService.findExpiredProducts();
    }

    @PostMapping
    public ProductExpiration createProduct(@Valid @RequestBody CreateProductRequest request) {
        return expirationCheckService.createProduct(
                request.productCode(),
                request.productName(),
                request.expirationDate());
    }

    public record CreateProductRequest(
            @NotBlank String productCode,
            @NotBlank String productName,
            @NotNull LocalDate expirationDate
    ) {
    }
}
