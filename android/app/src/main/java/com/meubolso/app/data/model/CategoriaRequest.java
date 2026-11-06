package com.meubolso.app.data.model;

import java.math.BigDecimal;

public class CategoriaRequest {

    private final String nome;
    private final TipoTransacao tipo;
    private final String cor;
    private final BigDecimal limiteMensal;

    public CategoriaRequest(String nome, TipoTransacao tipo, String cor, BigDecimal limiteMensal) {
        this.nome = nome;
        this.tipo = tipo;
        this.cor = cor;
        this.limiteMensal = limiteMensal;
    }

    public String getNome() {
        return nome;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public String getCor() {
        return cor;
    }

    public BigDecimal getLimiteMensal() {
        return limiteMensal;
    }
}
