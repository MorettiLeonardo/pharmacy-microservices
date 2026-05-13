package com.pharmacy.sales.api;

import com.pharmacy.sales.domain.Sale;
import com.pharmacy.sales.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody CreateSaleRequest request) {
        Sale createdSale = saleService.createSale(request.productId(), request.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(SaleResponse.from(createdSale));
    }

    @GetMapping
    public List<SaleResponse> listSales() {
        return saleService.findAll().stream()
                .map(SaleResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public SaleResponse getSaleById(@PathVariable Long id) {
        return SaleResponse.from(saleService.findById(id));
    }
}
