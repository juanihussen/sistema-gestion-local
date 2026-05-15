package com.gestion.local.controller;

import com.gestion.local.model.CashFlow;
import com.gestion.local.repository.CashFlowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cashflow")
@RequiredArgsConstructor
public class CashFlowController {

    private final CashFlowRepository cashFlowRepository;

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance() {
        BigDecimal ingresos = cashFlowRepository.sumByTipo(com.gestion.local.model.CashFlowType.INGRESO);
        BigDecimal egresos = cashFlowRepository.sumByTipo(com.gestion.local.model.CashFlowType.EGRESO);
        BigDecimal balance = ingresos.subtract(egresos);

        return ResponseEntity.ok(Map.of(
                "ingresos", ingresos,
                "egresos", egresos,
                "balance", balance
        ));
    }

    @GetMapping("/entries")
    public ResponseEntity<List<CashFlow>> getAllEntries() {
        return ResponseEntity.ok(cashFlowRepository.findAll());
    }
}
