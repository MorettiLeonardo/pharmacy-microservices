package com.pharmacy.expiration.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "product_expirations")
public class ProductExpiration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String productCode;

    @Column(nullable = false, length = 255)
    private String productName;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(nullable = false)
    private Instant lastStatusUpdate = Instant.now();

    protected ProductExpiration() {
    }

    public ProductExpiration(String productCode, String productName, LocalDate expirationDate) {
        this.productCode = productCode;
        this.productName = productName;
        this.expirationDate = expirationDate;
    }

    public Long getId() {
        return id;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public Instant getLastStatusUpdate() {
        return lastStatusUpdate;
    }

    public void updateFromInventory(String productName, LocalDate expirationDate) {
        this.productName = productName;
        this.expirationDate = expirationDate;
        this.lastStatusUpdate = Instant.now();
    }

    public void markExpired() {
        this.status = ProductStatus.EXPIRED;
        this.lastStatusUpdate = Instant.now();
    }
}
