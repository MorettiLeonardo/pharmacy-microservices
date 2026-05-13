package com.pharmacy.inventory.web;

import com.pharmacy.inventory.domain.Product;
import com.pharmacy.inventory.service.ProductService;
import com.pharmacy.inventory.web.dto.CreateProductRequest;
import com.pharmacy.inventory.web.dto.ProductResponse;
import com.pharmacy.inventory.web.dto.UpdateStockRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setStock(request.stock());
        product.setExpirationDate(request.expirationDate());
        product.setControlled(request.controlled());
        Product created = productService.create(product);
        return ResponseEntity.created(URI.create("/products/" + created.getId())).body(ProductResponse.from(created));
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findAll().stream().map(ProductResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return ProductResponse.from(productService.findById(id));
    }

    @PutMapping("/{id}/stock")
    public ProductResponse updateStock(@PathVariable Long id, @Valid @RequestBody UpdateStockRequest request) {
        return ProductResponse.from(productService.updateStock(id, request.stock()));
    }
}
