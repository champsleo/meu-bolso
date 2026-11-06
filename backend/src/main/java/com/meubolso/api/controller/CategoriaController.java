package com.meubolso.api.controller;

import com.meubolso.api.dto.CategoriaDtos.CategoriaRequest;
import com.meubolso.api.dto.CategoriaDtos.CategoriaResponse;
import com.meubolso.api.model.TipoTransacao;
import com.meubolso.api.security.UsuarioAutenticado;
import com.meubolso.api.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * CRUD de categorias. O controller só recebe a requisição e repassa para o service:
 * nenhuma regra de negócio fica aqui.
 */
@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Categorias de receitas e despesas")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @Operation(summary = "Lista as categorias do usuário (filtro opcional por tipo)")
    public ResponseEntity<List<CategoriaResponse>> listar(
            @RequestParam(required = false) TipoTransacao tipo,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(categoriaService.listar(usuario.id(), tipo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma categoria pelo id")
    public ResponseEntity<CategoriaResponse> buscar(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(categoriaService.buscar(id, usuario.id()));
    }

    @PostMapping
    @Operation(summary = "Cria uma categoria")
    public ResponseEntity<CategoriaResponse> criar(
            @Valid @RequestBody CategoriaRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        CategoriaResponse criada = categoriaService.criar(request, usuario.id());
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(local).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma categoria")
    public ResponseEntity<CategoriaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(categoriaService.atualizar(id, request, usuario.id()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma categoria (somente se não tiver lançamentos)")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        categoriaService.excluir(id, usuario.id());
        return ResponseEntity.noContent().build();
    }
}
