package com.pharmacy.sales.service;

import com.pharmacy.sales.domain.Sale;
import com.pharmacy.sales.messaging.SaleCreatedProducer;
import com.pharmacy.sales.repository.SaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SaleService {

    private static final Logger log = LoggerFactory.getLogger(SaleService.class);

    private final SaleRepository saleRepository;
    private final SaleCreatedProducer saleCreatedProducer;

    public SaleService(SaleRepository saleRepository, SaleCreatedProducer saleCreatedProducer) {
        this.saleRepository = saleRepository;
        this.saleCreatedProducer = saleCreatedProducer;
    }

    @Transactional
    public Sale createSale(Long productId, Integer quantity) {
        Sale sale = new Sale();
        sale.setProductId(productId);
        sale.setQuantity(quantity);

        Sale createdSale = saleRepository.save(sale);
        log.info("NOVA VENDA: Produto ID {}, Quantidade {}", 
                createdSale.getProductId(), createdSale.getQuantity());

        saleCreatedProducer.publish(createdSale);
        return createdSale;
    }

    @Transactional(readOnly = true)
    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Sale findById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda não encontrada com id=" + id));
    }

    @Transactional
    public Sale updateSale(Long id, Integer quantity) {
        Sale sale = findById(id);
        sale.setQuantity(quantity);
        Sale updatedSale = saleRepository.save(sale);
        log.info("VENDA ATUALIZADA: ID {}, Nova Quantidade {}", id, quantity);
        return updatedSale;
    }

    @Transactional
    public void deleteSale(Long id) {
        Sale sale = findById(id);
        saleRepository.delete(sale);
        log.info("VENDA CANCELADA: ID {}", id);
    }
}
