package com.meubolso.app.data.model;

import java.math.BigDecimal;
import java.util.List;

public class ResumoMensal {

    private String mes;
    private BigDecimal totalReceitas;
    private BigDecimal totalDespesas;
    private BigDecimal saldo;
    private int quantidadeLancamentos;
    private List<GastoCategoria> despesasPorCategoria;

    public String getMes() {
        return mes;
    }

    public BigDecimal getTotalReceitas() {
        return totalReceitas;
    }

    public BigDecimal getTotalDespesas() {
        return totalDespesas;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public int getQuantidadeLancamentos() {
        return quantidadeLancamentos;
    }

    public List<GastoCategoria> getDespesasPorCategoria() {
        return despesasPorCategoria;
    }
}
