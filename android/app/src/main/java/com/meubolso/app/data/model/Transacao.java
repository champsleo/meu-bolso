package com.meubolso.app.data.model;

import java.math.BigDecimal;
import java.util.Objects;

/** Um lançamento (receita ou despesa), exatamente como a API devolve. */
public class Transacao {

    private Long id;
    private String descricao;
    private BigDecimal valor;
    /** Data no formato AAAA-MM-DD (ex.: 2026-11-02). */
    private String data;
    private TipoTransacao tipo;
    private Long categoriaId;
    private String categoriaNome;
    private String categoriaCor;

    public Long getId() {
        return id;
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

    public TipoTransacao getTipo() {
        return tipo;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public String getCategoriaNome() {
        return categoriaNome;
    }

    public String getCategoriaCor() {
        return categoriaCor;
    }

    /** Usado pelo DiffUtil da lista para saber se um item mudou. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transacao)) return false;
        Transacao outra = (Transacao) o;
        return Objects.equals(id, outra.id)
                && Objects.equals(descricao, outra.descricao)
                && Objects.equals(valor, outra.valor)
                && Objects.equals(data, outra.data)
                && tipo == outra.tipo
                && Objects.equals(categoriaId, outra.categoriaId)
                && Objects.equals(categoriaNome, outra.categoriaNome)
                && Objects.equals(categoriaCor, outra.categoriaCor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, descricao, valor, data, tipo, categoriaId);
    }
}
