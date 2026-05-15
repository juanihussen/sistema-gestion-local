package com.gestion.local.repository;

import com.gestion.local.model.CashFlow;
import com.gestion.local.model.CashFlowType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CashFlowRepository extends JpaRepository<CashFlow, Long> {

    @Query("SELECT COALESCE(SUM(c.monto), 0) FROM CashFlow c WHERE c.tipo = :tipo")
    BigDecimal sumByTipo(@Param("tipo") CashFlowType tipo);
}
