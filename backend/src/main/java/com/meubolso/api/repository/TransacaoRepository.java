package com.meubolso.api.repository;

import com.meubolso.api.model.TipoTransacao;
import com.meubolso.api.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioIdAndDataBetweenOrderByDataDescIdDesc(
            Long usuarioId, LocalDate inicio, LocalDate fim);

    List<Transacao> findByUsuarioIdAndTipoAndDataBetweenOrderByDataDescIdDesc(
            Long usuarioId, TipoTransacao tipo, LocalDate inicio, LocalDate fim);

    Optional<Transacao> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByCategoriaId(Long categoriaId);
}
