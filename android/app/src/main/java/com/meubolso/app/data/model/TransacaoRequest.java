package com.meubolso.app.data.model;

import java.math.BigDecimal;

public class TransacaoRequest {

    private final String descricao;
    private final BigDecimal valor;
    /** Formato AAAA-MM-DD. */
    private final String data;
    private final Long categoriaId;

    public TransacaoRequest(String descricao, BigDecimal valor, String data, Long categoriaId) {
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.categoriaId = categoriaId;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getData() {
        return data;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }
}
