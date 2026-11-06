package com.meubolso.api.dto;

import java.math.BigDecimal;
import java.util.List;

public final class ResumoDtos {

    private ResumoDtos() {
    }

    /**
     * Resumo de um mês: totais, saldo e quanto foi gasto em cada categoria.
     */
    public record ResumoMensalResponse(
            String mes,
            BigDecimal totalReceitas,
            BigDecimal totalDespesas,
            BigDecimal saldo,
            int quantidadeLancamentos,
            List<GastoPorCategoria> despesasPorCategoria) {
    }

    /**
     * @param percentualDoTotal  quanto essa categoria representa do total de despesas (0 a 100)
     * @param percentualDoLimite quanto do limite mensal já foi usado (null se a categoria não tem limite)
     */
    public record GastoPorCategoria(
            Long categoriaId,
            String nome,
            String cor,
            BigDecimal total,
            double percentualDoTotal,
            BigDecimal limiteMensal,
            Double percentualDoLimite,
            boolean limiteExcedido) {
    }
}
