package com.gestion.local.repository;

import com.gestion.local.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.supplier WHERE p.stock <= p.stockMinimo")
    List<Product> findProductsWithLowStock();
}
