package com.meubolso.api.dto;

import com.meubolso.api.model.TipoTransacao;
import com.meubolso.api.model.Transacao;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class TransacaoDtos {

    private TransacaoDtos() {
    }

    /** O tipo não é enviado: ele vem da categoria escolhida. */
    public record TransacaoRequest(
            @NotBlank(message = "Informe a descrição")
            @Size(max = 100, message = "A descrição deve ter no máximo 100 caracteres")
            String descricao,

            @NotNull(message = "Informe o valor")
            @Positive(message = "O valor deve ser maior que zero")
            @Digits(integer = 10, fraction = 2, message = "Use no máximo 2 casas decimais")
            BigDecimal valor,

            @NotNull(message = "Informe a data")
            LocalDate data,

            @NotNull(message = "Informe a categoria")
            Long categoriaId) {
    }

    public record TransacaoResponse(
            Long id,
            String descricao,
            BigDecimal valor,
            LocalDate data,
            TipoTransacao tipo,
            Long categoriaId,
            String categoriaNome,
            String categoriaCor) {

        public static TransacaoResponse de(Transacao t) {
            return new TransacaoResponse(
                    t.getId(),
                    t.getDescricao(),
                    t.getValor(),
                    t.getData(),
                    t.getTipo(),
                    t.getCategoria().getId(),
                    t.getCategoria().getNome(),
                    t.getCategoria().getCor());
        }
    }
}
