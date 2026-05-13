package com.pharmacy.inventory.service;

import com.pharmacy.inventory.domain.Product;
import com.pharmacy.inventory.exception.InsufficientStockException;
import com.pharmacy.inventory.exception.ProductNotFoundException;
import com.pharmacy.inventory.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public Product updateStock(Long id, Integer stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        Product product = findById(id);
        product.setStock(stock);
        return product;
    }

    @Transactional
    public Product decrementStock(Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Product product = findById(productId);
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(productId, product.getStock(), quantity);
        }
        product.setStock(product.getStock() - quantity);
        return product;
    }
}
