package com.meubolso.app.data.model;

/** Resposta do login/cadastro: o token JWT e os dados do usuário. */
public class AuthResponse {

    private String token;
    private Usuario usuario;

    public String getToken() {
        return token;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
