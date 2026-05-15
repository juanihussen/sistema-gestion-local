package com.gestion.local.controller;

import com.gestion.local.model.Product;
import com.gestion.local.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AlertController {

    private final ProductService productService;

    @GetMapping("/alerts")
    public ResponseEntity<List<Product>> getStockAlerts() {
        List<Product> alerts = productService.checkStockAlerts();
        return ResponseEntity.ok(alerts);
    }
}
