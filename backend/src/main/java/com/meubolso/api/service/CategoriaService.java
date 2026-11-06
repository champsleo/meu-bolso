package com.meubolso.api.service;

import com.meubolso.api.dto.CategoriaDtos.CategoriaRequest;
import com.meubolso.api.dto.CategoriaDtos.CategoriaResponse;
import com.meubolso.api.exception.ConflitoException;
import com.meubolso.api.exception.RecursoNaoEncontradoException;
import com.meubolso.api.exception.RegraNegocioException;
import com.meubolso.api.model.Categoria;
import com.meubolso.api.model.TipoTransacao;
import com.meubolso.api.model.Usuario;
import com.meubolso.api.repository.CategoriaRepository;
import com.meubolso.api.repository.TransacaoRepository;
import com.meubolso.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Regras de negócio das categorias.
 */
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public CategoriaService(CategoriaRepository categoriaRepository,
                            TransacaoRepository transacaoRepository,
                            UsuarioRepository usuarioRepository) {
        this.categoriaRepository = categoriaRepository;
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar(Long usuarioId, TipoTransacao tipo) {
        List<Categoria> categorias = (tipo == null)
                ? categoriaRepository.findByUsuarioIdOrderByNomeAsc(usuarioId)
                : categoriaRepository.findByUsuarioIdAndTipoOrderByNomeAsc(usuarioId, tipo);
        return categorias.stream().map(CategoriaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscar(Long id, Long usuarioId) {
        return CategoriaResponse.de(buscarEntidade(id, usuarioId));
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request, Long usuarioId) {
        validarLimite(request);
        String nome = request.nome().trim();
        if (categoriaRepository.existsByUsuarioIdAndTipoAndNomeIgnoreCase(usuarioId, request.tipo(), nome)) {
            throw new ConflitoException("Já existe uma categoria com esse nome");
        }

        Usuario usuario = usuarioRepository.getReferenceById(usuarioId);
        Categoria categoria = new Categoria(nome, request.tipo(), corMaiuscula(request.cor()),
                request.limiteMensal(), usuario);
        return CategoriaResponse.de(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest request, Long usuarioId) {
        validarLimite(request);
        Categoria categoria = buscarEntidade(id, usuarioId);
        String nome = request.nome().trim();

        if (categoriaRepository.existsByUsuarioIdAndTipoAndNomeIgnoreCaseAndIdNot(usuarioId, request.tipo(), nome, id)) {
            throw new ConflitoException("Já existe uma categoria com esse nome");
        }
        if (categoria.getTipo() != request.tipo() && transacaoRepository.existsByCategoriaId(id)) {
            throw new RegraNegocioException(
                    "Não é possível mudar o tipo de uma categoria que já possui lançamentos");
        }

        categoria.atualizar(nome, request.tipo(), corMaiuscula(request.cor()), request.limiteMensal());
        return CategoriaResponse.de(categoria);
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        Categoria categoria = buscarEntidade(id, usuarioId);
        if (transacaoRepository.existsByCategoriaId(id)) {
            throw new RegraNegocioException(
                    "Esta categoria possui lançamentos. Exclua ou altere os lançamentos antes.");
        }
        categoriaRepository.delete(categoria);
    }

    /** Usado também pelo TransacaoService. Garante que a categoria pertence ao usuário. */
    @Transactional(readOnly = true)
    public Categoria buscarEntidade(Long id, Long usuarioId) {
        return categoriaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));
    }

    @Transactional
    public void criarCategoriasPadrao(Usuario usuario) {
        List<Categoria> padrao = List.of(
                new Categoria("Alimentação", TipoTransacao.DESPESA, "#EF5350", null, usuario),
                new Categoria("Transporte", TipoTransacao.DESPESA, "#42A5F5", null, usuario),
                new Categoria("Moradia", TipoTransacao.DESPESA, "#8D6E63", null, usuario),
                new Categoria("Lazer", TipoTransacao.DESPESA, "#AB47BC", null, usuario),
                new Categoria("Saúde", TipoTransacao.DESPESA, "#26A69A", null, usuario),
                new Categoria("Educação", TipoTransacao.DESPESA, "#FFA726", null, usuario),
                new Categoria("Outras despesas", TipoTransacao.DESPESA, "#78909C", null, usuario),
                new Categoria("Salário", TipoTransacao.RECEITA, "#66BB6A", null, usuario),
                new Categoria("Freelance", TipoTransacao.RECEITA, "#29B6F6", null, usuario),
                new Categoria("Outras receitas", TipoTransacao.RECEITA, "#9CCC65", null, usuario));
        categoriaRepository.saveAll(padrao);
    }

    private void validarLimite(CategoriaRequest request) {
        if (request.tipo() == TipoTransacao.RECEITA && request.limiteMensal() != null) {
            throw new RegraNegocioException("Somente categorias de despesa podem ter limite mensal");
        }
    }

    private String corMaiuscula(String cor) {
        return cor.toUpperCase(Locale.ROOT);
    }
}
