package com.meubolso.api.repository;

import com.meubolso.api.model.Categoria;
import com.meubolso.api.model.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * O Spring Data gera o SQL automaticamente a partir do nome dos métodos.
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByUsuarioIdOrderByNomeAsc(Long usuarioId);

    List<Categoria> findByUsuarioIdAndTipoOrderByNomeAsc(Long usuarioId, TipoTransacao tipo);

    Optional<Categoria> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByUsuarioIdAndTipoAndNomeIgnoreCase(Long usuarioId, TipoTransacao tipo, String nome);

    boolean existsByUsuarioIdAndTipoAndNomeIgnoreCaseAndIdNot(Long usuarioId, TipoTransacao tipo, String nome, Long id);
}
