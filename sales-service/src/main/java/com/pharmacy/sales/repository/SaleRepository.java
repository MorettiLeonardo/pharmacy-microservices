package com.pharmacy.sales.repository;

import com.pharmacy.sales.domain.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}
