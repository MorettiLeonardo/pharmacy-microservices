package com.pharmacy.expiration.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductExpirationRepository extends JpaRepository<ProductExpiration, Long> {

    List<ProductExpiration> findByExpirationDateBeforeAndStatusNot(LocalDate date, ProductStatus status);

    List<ProductExpiration> findByStatus(ProductStatus status);

    boolean existsByProductCode(String productCode);

    Optional<ProductExpiration> findByProductCode(String productCode);
}
