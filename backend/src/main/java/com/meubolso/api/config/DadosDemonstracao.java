package com.meubolso.api.config;

import com.meubolso.api.dto.AuthDtos.CadastroRequest;
import com.meubolso.api.dto.CategoriaDtos.CategoriaRequest;
import com.meubolso.api.dto.CategoriaDtos.CategoriaResponse;
import com.meubolso.api.dto.TransacaoDtos.TransacaoRequest;
import com.meubolso.api.model.TipoTransacao;
import com.meubolso.api.repository.UsuarioRepository;
import com.meubolso.api.service.AuthService;
import com.meubolso.api.service.CategoriaService;
import com.meubolso.api.service.TransacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Cria um usuário de demonstração com lançamentos de exemplo na primeira vez que a API sobe.
 * Login: demo@meubolso.com / senha: 123456
 * Para desligar, use app.demo.habilitado=false no application.properties.
 */
@Component
@ConditionalOnProperty(name = "app.demo.habilitado", havingValue = "true")
public class DadosDemonstracao implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosDemonstracao.class);
    private static final String EMAIL_DEMO = "demo@meubolso.com";

    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;
    private final CategoriaService categoriaService;
    private final TransacaoService transacaoService;

    public DadosDemonstracao(UsuarioRepository usuarioRepository,
                             AuthService authService,
                             CategoriaService categoriaService,
                             TransacaoService transacaoService) {
        this.usuarioRepository = usuarioRepository;
        this.authService = authService;
        this.categoriaService = categoriaService;
        this.transacaoService = transacaoService;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByEmailIgnoreCase(EMAIL_DEMO)) {
            return;
        }

        Long usuarioId = authService.cadastrar(
                new CadastroRequest("Usuário Demo", EMAIL_DEMO, "123456")).usuario().id();

        Map<String, Long> categorias = categoriaService.listar(usuarioId, null).stream()
                .collect(Collectors.toMap(CategoriaResponse::nome, CategoriaResponse::id, (a, b) -> a));

        // Define limites mensais em duas categorias para demonstrar o alerta de limite
        definirLimite(usuarioId, categorias.get("Alimentação"), "Alimentação", "#EF5350", "600.00");
        definirLimite(usuarioId, categorias.get("Lazer"), "Lazer", "#AB47BC", "150.00");

        YearMonth atual = YearMonth.now();
        YearMonth anterior = atual.minusMonths(1);
        Function<String, Long> cat = categorias::get;

        // Mês anterior
        lancar(usuarioId, "Salário", "3200.00", anterior, 5, cat.apply("Salário"));
        lancar(usuarioId, "Aluguel", "1100.00", anterior, 6, cat.apply("Moradia"));
        lancar(usuarioId, "Supermercado", "420.50", anterior, 8, cat.apply("Alimentação"));
        lancar(usuarioId, "Cinema", "60.00", anterior, 14, cat.apply("Lazer"));
        lancar(usuarioId, "Ônibus", "180.00", anterior, 10, cat.apply("Transporte"));
        lancar(usuarioId, "Site para cliente", "850.00", anterior, 20, cat.apply("Freelance"));

        // Mês atual
        lancar(usuarioId, "Salário", "3200.00", atual, 1, cat.apply("Salário"));
        lancar(usuarioId, "Aluguel", "1100.00", atual, 1, cat.apply("Moradia"));
        lancar(usuarioId, "Supermercado", "385.90", atual, 2, cat.apply("Alimentação"));
        lancar(usuarioId, "Restaurante", "248.00", atual, 2, cat.apply("Alimentação"));
        lancar(usuarioId, "Show", "180.00", atual, 2, cat.apply("Lazer"));
        lancar(usuarioId, "Combustível", "150.00", atual, 1, cat.apply("Transporte"));
        lancar(usuarioId, "Farmácia", "72.40", atual, 2, cat.apply("Saúde"));
        lancar(usuarioId, "Curso online", "99.90", atual, 1, cat.apply("Educação"));

        log.info("Usuário de demonstração criado: {} / senha 123456", EMAIL_DEMO);
    }

    private void definirLimite(Long usuarioId, Long categoriaId, String nome, String cor, String limite) {
        categoriaService.atualizar(categoriaId,
                new CategoriaRequest(nome, TipoTransacao.DESPESA, cor, new BigDecimal(limite)), usuarioId);
    }

    private void lancar(Long usuarioId, String descricao, String valor, YearMonth mes, int dia, Long categoriaId) {
        int diaValido = Math.min(dia, mes.lengthOfMonth());
        transacaoService.criar(
                new TransacaoRequest(descricao, new BigDecimal(valor), mes.atDay(diaValido), categoriaId),
                usuarioId);
    }
}
