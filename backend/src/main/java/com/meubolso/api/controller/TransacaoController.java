package com.meubolso.api.controller;

import com.meubolso.api.dto.TransacaoDtos.TransacaoRequest;
import com.meubolso.api.dto.TransacaoDtos.TransacaoResponse;
import com.meubolso.api.model.TipoTransacao;
import com.meubolso.api.security.UsuarioAutenticado;
import com.meubolso.api.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
@Tag(name = "Lançamentos", description = "Receitas e despesas")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping
    @Operation(summary = "Lista os lançamentos de um mês (ex.: ?mes=2026-11&tipo=DESPESA)")
    public ResponseEntity<List<TransacaoResponse>> listar(
            @Parameter(description = "Mês no formato AAAA-MM. Se não informado, usa o mês atual.")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes,
            @RequestParam(required = false) TipoTransacao tipo,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        YearMonth mesConsultado = (mes != null) ? mes : YearMonth.now();
        return ResponseEntity.ok(transacaoService.listar(usuario.id(), mesConsultado, tipo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um lançamento pelo id")
    public ResponseEntity<TransacaoResponse> buscar(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(transacaoService.buscar(id, usuario.id()));
    }

    @PostMapping
    @Operation(summary = "Cria um lançamento")
    public ResponseEntity<TransacaoResponse> criar(
            @Valid @RequestBody TransacaoRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        TransacaoResponse criada = transacaoService.criar(request, usuario.id());
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(local).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um lançamento")
    public ResponseEntity<TransacaoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TransacaoRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(transacaoService.atualizar(id, request, usuario.id()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um lançamento")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        transacaoService.excluir(id, usuario.id());
        return ResponseEntity.noContent().build();
    }
}
