package com.pharmacy.inventory.service;

import com.pharmacy.inventory.domain.Product;
import com.pharmacy.inventory.exception.InsufficientStockException;
import com.pharmacy.inventory.exception.ProductNotFoundException;
import com.pharmacy.inventory.messaging.ProductCreatedProducer;
import com.pharmacy.inventory.repository.ProductRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductCreatedProducer productCreatedProducer;

    public ProductService(ProductRepository productRepository, ProductCreatedProducer productCreatedProducer) {
        this.productRepository = productRepository;
        this.productCreatedProducer = productCreatedProducer;
    }

    public Product create(Product product) {
        Product createdProduct = productRepository.save(product);
        log.info("NOVO PRODUTO CADASTRADO: {} (Estoque inicial: {})", 
                createdProduct.getName(), createdProduct.getStock());
        productCreatedProducer.publish(createdProduct);
        return createdProduct;
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
            throw new IllegalArgumentException("Estoque não pode ser negativo");
        }
        Product product = findById(id);
        product.setStock(stock);
        log.info("ESTOQUE ATUALIZADO: {} agora possui {} unidades", product.getName(), stock);
        return product;
    }

    @Transactional
    public Product decrementStock(Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        Product product = findById(productId);
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(productId, product.getStock(), quantity);
        }
        product.setStock(product.getStock() - quantity);
        log.info("SAÍDA DE ESTOQUE: {} (-{} unidades, Restante: {})", 
                product.getName(), quantity, product.getStock());
        return product;
    }

    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
        log.info("PRODUTO REMOVIDO: {}", product.getName());
    }
}
