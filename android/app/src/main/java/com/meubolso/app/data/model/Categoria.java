package com.meubolso.app.data.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/** Serializable para poder ser enviada entre telas dentro de um Bundle. */
public class Categoria implements Serializable {

    private Long id;
    private String nome;
    private TipoTransacao tipo;
    private String cor;
    private BigDecimal limiteMensal;

    public Long getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categoria)) return false;
        Categoria outra = (Categoria) o;
        return Objects.equals(id, outra.id)
                && Objects.equals(nome, outra.nome)
                && tipo == outra.tipo
                && Objects.equals(cor, outra.cor)
                && Objects.equals(limiteMensal, outra.limiteMensal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, tipo, cor, limiteMensal);
    }

    /** Usado pelo dropdown de categorias: exibe o nome. */
    @Override
    public String toString() {
        return nome;
    }
}
