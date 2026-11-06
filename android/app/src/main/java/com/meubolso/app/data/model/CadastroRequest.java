package com.meubolso.app.data.model;

public class CadastroRequest {

    private final String nome;
    private final String email;
    private final String senha;

    public CadastroRequest(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}
