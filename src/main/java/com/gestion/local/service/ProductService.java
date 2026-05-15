package com.gestion.local.service;

import com.gestion.local.model.*;
import com.gestion.local.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;
    private final CashFlowRepository cashFlowRepository;

    public record InvoiceItemRequest(Long productId, Integer cantidad) {}

    @Transactional
    public Invoice createInvoice(String nroFactura, List<InvoiceItemRequest> items) {
        List<Long> productIds = items.stream().map(InvoiceItemRequest::productId).toList();
        List<Product> products = productRepository.findAllById(productIds);

        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("Uno o más productos no fueron encontrados");
        }

        Map<Long, Product> productMap = new HashMap<>();
        products.forEach(p -> productMap.put(p.getId(), p));

        Invoice invoice = Invoice.builder()
                .nroFactura(nroFactura)
                .fechaCreacion(LocalDateTime.now())
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (InvoiceItemRequest req : items) {
            Product product = productMap.get(req.productId());

            if (product.getStock() < req.cantidad()) {
                throw new IllegalStateException(
                        "Stock insuficiente para " + product.getNombre()
                                + ": disponible " + product.getStock()
                                + ", solicitado " + req.cantidad());
            }

            product.setStock(product.getStock() - req.cantidad());

            InvoiceItem item = InvoiceItem.builder()
                    .cantidad(req.cantidad())
                    .precioUnitarioMomento(product.getPrecioVenta())
                    .product(product)
                    .invoice(invoice)
                    .build();

            invoice.getItems().add(item);
            total = total.add(product.getPrecioVenta().multiply(BigDecimal.valueOf(req.cantidad())));
        }

        invoice.setTotal(total);
        Invoice saved = invoiceRepository.save(invoice);

        registerCashFlow(total, "Venta factura " + nroFactura);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Product> checkStockAlerts() {
        return productRepository.findProductsWithLowStock();
    }

    @Transactional
    public CashFlow registerCashFlow(BigDecimal monto, String descripcion) {
        CashFlow cashFlow = CashFlow.builder()
                .fecha(LocalDate.now())
                .tipo(CashFlowType.INGRESO)
                .monto(monto)
                .descripcion(descripcion)
                .build();
        return cashFlowRepository.save(cashFlow);
    }
}
