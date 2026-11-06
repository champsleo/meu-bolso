package com.meubolso.api.controller;

import com.meubolso.api.dto.ResumoDtos.ResumoMensalResponse;
import com.meubolso.api.security.UsuarioAutenticado;
import com.meubolso.api.service.ResumoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/resumo")
@Tag(name = "Resumo", description = "Totais, saldo e gastos por categoria")
public class ResumoController {

    private final ResumoService resumoService;

    public ResumoController(ResumoService resumoService) {
        this.resumoService = resumoService;
    }

    @GetMapping
    @Operation(summary = "Resumo financeiro do mês (ex.: ?mes=2026-11)")
    public ResponseEntity<ResumoMensalResponse> resumo(
            @Parameter(description = "Mês no formato AAAA-MM. Se não informado, usa o mês atual.")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        YearMonth mesConsultado = (mes != null) ? mes : YearMonth.now();
        return ResponseEntity.ok(resumoService.gerar(usuario.id(), mesConsultado));
    }
}
