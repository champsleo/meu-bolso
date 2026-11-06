package com.meubolso.api.security;

/**
 * Dados do usuário logado, extraídos do token JWT.
 * Os controllers recebem este objeto com @AuthenticationPrincipal.
 */
public record UsuarioAutenticado(Long id, String email) {
}
