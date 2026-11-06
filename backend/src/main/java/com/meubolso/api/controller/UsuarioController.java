package com.meubolso.api.controller;

import com.meubolso.api.dto.AuthDtos.UsuarioResponse;
import com.meubolso.api.security.UsuarioAutenticado;
import com.meubolso.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários")
public class UsuarioController {

    private final AuthService authService;

    public UsuarioController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    @Operation(summary = "Retorna os dados do usuário logado")
    public ResponseEntity<UsuarioResponse> meusDados(@Parameter(hidden = true)
                                                     @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(authService.buscarUsuario(usuario.id()));
    }
}
