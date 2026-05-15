package com.gestion.local.controller;

import com.gestion.local.model.Product;
import com.gestion.local.model.Supplier;
import com.gestion.local.repository.ProductRepository;
import com.gestion.local.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public record ProductRequest(
            String nombre,
            String categoria,
            Integer stock,
            Integer stockMinimo,
            java.math.BigDecimal precioVenta,
            java.math.BigDecimal precioCosto,
            Long supplierId
    ) {}

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest request) {
        Supplier supplier = null;
        if (request.supplierId() != null) {
            supplier = supplierRepository.findById(request.supplierId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Supplier not found with id: " + request.supplierId()));
        }

        Product product = Product.builder()
                .nombre(request.nombre())
                .categoria(request.categoria())
                .stock(request.stock())
                .stockMinimo(request.stockMinimo())
                .precioVenta(request.precioVenta())
                .precioCosto(request.precioCosto())
                .supplier(supplier)
                .build();

        Product saved = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        Supplier supplier = product.getSupplier();
        if (request.supplierId() != null) {
            supplier = supplierRepository.findById(request.supplierId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + request.supplierId()));
        }

        if (request.nombre() != null) product.setNombre(request.nombre());
        if (request.categoria() != null) product.setCategoria(request.categoria());
        if (request.stock() != null) product.setStock(request.stock());
        if (request.stockMinimo() != null) product.setStockMinimo(request.stockMinimo());
        if (request.precioVenta() != null) product.setPrecioVenta(request.precioVenta());
        if (request.precioCosto() != null) product.setPrecioCosto(request.precioCosto());
        product.setSupplier(supplier);

        Product saved = productRepository.save(product);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
