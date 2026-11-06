package com.meubolso.api.service;

import com.meubolso.api.dto.TransacaoDtos.TransacaoRequest;
import com.meubolso.api.dto.TransacaoDtos.TransacaoResponse;
import com.meubolso.api.exception.RecursoNaoEncontradoException;
import com.meubolso.api.model.Categoria;
import com.meubolso.api.model.TipoTransacao;
import com.meubolso.api.model.Transacao;
import com.meubolso.api.model.Usuario;
import com.meubolso.api.repository.TransacaoRepository;
import com.meubolso.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Regras de negócio dos lançamentos (receitas e despesas).
 */
@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaService categoriaService;

    public TransacaoService(TransacaoRepository transacaoRepository,
                            UsuarioRepository usuarioRepository,
                            CategoriaService categoriaService) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaService = categoriaService;
    }

    @Transactional(readOnly = true)
    public List<TransacaoResponse> listar(Long usuarioId, YearMonth mes, TipoTransacao tipo) {
        LocalDate inicio = mes.atDay(1);
        LocalDate fim = mes.atEndOfMonth();

        List<Transacao> transacoes = (tipo == null)
                ? transacaoRepository.findByUsuarioIdAndDataBetweenOrderByDataDescIdDesc(usuarioId, inicio, fim)
                : transacaoRepository.findByUsuarioIdAndTipoAndDataBetweenOrderByDataDescIdDesc(usuarioId, tipo, inicio, fim);

        return transacoes.stream().map(TransacaoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public TransacaoResponse buscar(Long id, Long usuarioId) {
        return TransacaoResponse.de(buscarEntidade(id, usuarioId));
    }

    @Transactional
    public TransacaoResponse criar(TransacaoRequest request, Long usuarioId) {
        Categoria categoria = categoriaService.buscarEntidade(request.categoriaId(), usuarioId);
        Usuario usuario = usuarioRepository.getReferenceById(usuarioId);

        Transacao transacao = new Transacao(
                request.descricao().trim(), request.valor(), request.data(), categoria, usuario);
        return TransacaoResponse.de(transacaoRepository.save(transacao));
    }

    @Transactional
    public TransacaoResponse atualizar(Long id, TransacaoRequest request, Long usuarioId) {
        Transacao transacao = buscarEntidade(id, usuarioId);
        Categoria categoria = categoriaService.buscarEntidade(request.categoriaId(), usuarioId);

        transacao.atualizar(request.descricao().trim(), request.valor(), request.data(), categoria);
        return TransacaoResponse.de(transacao);
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        transacaoRepository.delete(buscarEntidade(id, usuarioId));
    }

    private Transacao buscarEntidade(Long id, Long usuarioId) {
        return transacaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lançamento não encontrado"));
    }
}
