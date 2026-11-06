package com.meubolso.app.data.model;

public class LoginRequest {

    private final String email;
    private final String senha;

    public LoginRequest(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}
