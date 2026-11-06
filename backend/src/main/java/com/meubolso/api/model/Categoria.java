package com.meubolso.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Agrupa lançamentos (ex.: Alimentação, Transporte, Salário).
 * Categorias de despesa podem ter um limite mensal de gastos.
 */
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoTransacao tipo;

    /** Cor em hexadecimal, usada no gráfico do app (ex.: #4CAF50). */
    @Column(nullable = false, length = 7)
    private String cor;

    /** Limite de gastos por mês. Opcional e só faz sentido para despesas. */
    @Column(name = "limite_mensal", precision = 12, scale = 2)
    private BigDecimal limiteMensal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    protected Categoria() {
        // exigido pelo JPA
    }

    public Categoria(String nome, TipoTransacao tipo, String cor, BigDecimal limiteMensal, Usuario usuario) {
        this.nome = nome;
        this.tipo = tipo;
        this.cor = cor;
        this.limiteMensal = limiteMensal;
        this.usuario = usuario;
    }

    public void atualizar(String nome, TipoTransacao tipo, String cor, BigDecimal limiteMensal) {
        this.nome = nome;
        this.tipo = tipo;
        this.cor = cor;
        this.limiteMensal = limiteMensal;
    }

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

    public Usuario getUsuario() {
        return usuario;
    }
}
