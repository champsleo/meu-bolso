package com.meubolso.api.dto;

import com.meubolso.api.model.Categoria;
import com.meubolso.api.model.TipoTransacao;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public final class CategoriaDtos {

    private CategoriaDtos() {
    }

    public record CategoriaRequest(
            @NotBlank(message = "Informe o nome da categoria")
            @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
            String nome,

            @NotNull(message = "Informe o tipo (RECEITA ou DESPESA)")
            TipoTransacao tipo,

            @NotBlank(message = "Informe a cor")
            @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "A cor deve estar no formato #RRGGBB")
            String cor,

            @Positive(message = "O limite mensal deve ser maior que zero")
            @Digits(integer = 10, fraction = 2, message = "Valor inválido")
            BigDecimal limiteMensal) {
    }

    public record CategoriaResponse(
            Long id,
            String nome,
            TipoTransacao tipo,
            String cor,
            BigDecimal limiteMensal) {

        public static CategoriaResponse de(Categoria categoria) {
            return new CategoriaResponse(
                    categoria.getId(),
                    categoria.getNome(),
                    categoria.getTipo(),
                    categoria.getCor(),
                    categoria.getLimiteMensal());
        }
    }
}
