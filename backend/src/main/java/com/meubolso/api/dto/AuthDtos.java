package com.meubolso.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTOs (Data Transfer Objects) de autenticação.
 * DTOs definem exatamente o JSON que entra e sai da API, sem expor as entidades do banco.
 */
public final class AuthDtos {

    private AuthDtos() {
    }

    public record CadastroRequest(
            @NotBlank(message = "Informe o nome")
            @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
            String nome,

            @NotBlank(message = "Informe o e-mail")
            @Email(message = "E-mail inválido")
            String email,

            @NotBlank(message = "Informe a senha")
            @Size(min = 6, max = 64, message = "A senha deve ter entre 6 e 64 caracteres")
            String senha) {
    }

    public record LoginRequest(
            @NotBlank(message = "Informe o e-mail")
            String email,

            @NotBlank(message = "Informe a senha")
            String senha) {
    }

    public record UsuarioResponse(Long id, String nome, String email) {
    }

    public record AuthResponse(String token, UsuarioResponse usuario) {
    }
}
