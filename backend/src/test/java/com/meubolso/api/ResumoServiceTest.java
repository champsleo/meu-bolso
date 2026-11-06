package com.meubolso.api;

import com.meubolso.api.dto.ResumoDtos.ResumoMensalResponse;
import com.meubolso.api.model.Categoria;
import com.meubolso.api.model.TipoTransacao;
import com.meubolso.api.model.Transacao;
import com.meubolso.api.model.Usuario;
import com.meubolso.api.repository.TransacaoRepository;
import com.meubolso.api.service.ResumoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Teste unitário: testa só a lógica de cálculo, sem banco de dados (o repositório é simulado com Mockito).
 */
class ResumoServiceTest {

    private final TransacaoRepository repository = mock(TransacaoRepository.class);
    private final ResumoService service = new ResumoService(repository);

    @Test
    void deveCalcularTotaisSaldoEPercentuais() {
        Usuario usuario = new Usuario("Teste", "t@t.com", "hash");
        Categoria salario = categoria(1L, "Salário", TipoTransacao.RECEITA, null, usuario);
        Categoria mercado = categoria(2L, "Mercado", TipoTransacao.DESPESA, new BigDecimal("200.00"), usuario);
        Categoria lazer = categoria(3L, "Lazer", TipoTransacao.DESPESA, null, usuario);
        LocalDate dia = LocalDate.of(2026, 11, 5);

        when(repository.findByUsuarioIdAndDataBetweenOrderByDataDescIdDesc(anyLong(), any(), any()))
                .thenReturn(List.of(
                        new Transacao("Salário", new BigDecimal("1000.00"), dia, salario, usuario),
                        new Transacao("Feira", new BigDecimal("150.00"), dia, mercado, usuario),
                        new Transacao("Mercado", new BigDecimal("150.00"), dia, mercado, usuario),
                        new Transacao("Cinema", new BigDecimal("100.00"), dia, lazer, usuario)));

        ResumoMensalResponse resumo = service.gerar(1L, YearMonth.of(2026, 11));

        assertThat(resumo.totalReceitas()).isEqualByComparingTo("1000.00");
        assertThat(resumo.totalDespesas()).isEqualByComparingTo("400.00");
        assertThat(resumo.saldo()).isEqualByComparingTo("600.00");
        assertThat(resumo.quantidadeLancamentos()).isEqualTo(4);

        // Ordenado do maior gasto para o menor
        assertThat(resumo.despesasPorCategoria()).hasSize(2);
        var primeiro = resumo.despesasPorCategoria().get(0);
        assertThat(primeiro.nome()).isEqualTo("Mercado");
        assertThat(primeiro.total()).isEqualByComparingTo("300.00");
        assertThat(primeiro.percentualDoTotal()).isEqualTo(75.0);
        assertThat(primeiro.percentualDoLimite()).isEqualTo(150.0);
        assertThat(primeiro.limiteExcedido()).isTrue();

        var segundo = resumo.despesasPorCategoria().get(1);
        assertThat(segundo.percentualDoLimite()).isNull();
        assertThat(segundo.limiteExcedido()).isFalse();
    }

    @Test
    void mesSemLancamentosDeveRetornarZeros() {
        when(repository.findByUsuarioIdAndDataBetweenOrderByDataDescIdDesc(anyLong(), any(), any()))
                .thenReturn(List.of());

        ResumoMensalResponse resumo = service.gerar(1L, YearMonth.of(2026, 1));

        assertThat(resumo.saldo()).isEqualByComparingTo("0");
        assertThat(resumo.despesasPorCategoria()).isEmpty();
    }

    private Categoria categoria(Long id, String nome, TipoTransacao tipo, BigDecimal limite, Usuario usuario) {
        Categoria categoria = new Categoria(nome, tipo, "#000000", limite, usuario);
        ReflectionTestUtils.setField(categoria, "id", id);
        return categoria;
    }
}
