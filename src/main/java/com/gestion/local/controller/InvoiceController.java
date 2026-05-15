package com.gestion.local.controller;

import com.gestion.local.model.Invoice;
import com.gestion.local.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final ProductService productService;

    public record InvoiceItemRequest(Long productId, Integer cantidad) {}

    public record InvoiceRequest(String nroFactura, List<InvoiceItemRequest> items) {}

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody InvoiceRequest request) {
        List<ProductService.InvoiceItemRequest> serviceItems = request.items().stream()
                .map(i -> new ProductService.InvoiceItemRequest(i.productId(), i.cantidad()))
                .toList();

        Invoice invoice = productService.createInvoice(request.nroFactura(), serviceItems);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }
}
