package com.pharmacy.expiration.service;

import com.pharmacy.expiration.exception.DuplicateProductCodeException;
import com.pharmacy.expiration.product.ProductExpiration;
import com.pharmacy.expiration.product.ProductExpirationRepository;
import com.pharmacy.expiration.product.ProductStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpirationCheckService {

    private static final Logger log = LoggerFactory.getLogger(ExpirationCheckService.class);

    private final ProductExpirationRepository productExpirationRepository;

    public ExpirationCheckService(ProductExpirationRepository productExpirationRepository) {
        this.productExpirationRepository = productExpirationRepository;
    }

    @Transactional
    public int processExpiredProducts() {
        List<ProductExpiration> productsToExpire = productExpirationRepository
                .findByExpirationDateBeforeAndStatusNot(LocalDate.now(), ProductStatus.EXPIRED);

        for (ProductExpiration product : productsToExpire) {
            product.markExpired();
            log.warn("Expired product detected: id={}, code={}, name={}, expirationDate={}",
                    product.getId(), product.getProductCode(), product.getProductName(), product.getExpirationDate());
        }

        return productsToExpire.size();
    }

    @Transactional(readOnly = true)
    public List<ProductExpiration> findAllProducts() {
        return productExpirationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ProductExpiration> findExpiredProducts() {
        return productExpirationRepository.findByStatus(ProductStatus.EXPIRED);
    }

    @Transactional
    public ProductExpiration createProduct(String productCode, String productName, LocalDate expirationDate) {
        if (productCode == null || productCode.isBlank()) {
            throw new IllegalArgumentException("productCode is required");
        }
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("productName is required");
        }
        if (expirationDate == null) {
            throw new IllegalArgumentException("expirationDate is required");
        }
        if (productExpirationRepository.existsByProductCode(productCode)) {
            throw new DuplicateProductCodeException(productCode);
        }
        ProductExpiration product = new ProductExpiration(productCode, productName, expirationDate);
        return productExpirationRepository.save(product);
    }
}
