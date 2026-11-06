package com.meubolso.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.meubolso.app.data.model.AuthResponse;

/**
 * Guarda o token e os dados do usuário logado no armazenamento local do celular (SharedPreferences),
 * para que a pessoa não precise fazer login toda vez que abrir o app.
 */
public class SessionManager {

    private static final String ARQUIVO = "meubolso_sessao";
    private static final String CHAVE_TOKEN = "token";
    private static final String CHAVE_NOME = "nome";
    private static final String CHAVE_EMAIL = "email";

    private final SharedPreferences preferencias;

    public SessionManager(Context context) {
        this.preferencias = context.getApplicationContext()
                .getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
    }

    public void salvar(AuthResponse resposta) {
        preferencias.edit()
                .putString(CHAVE_TOKEN, resposta.getToken())
                .putString(CHAVE_NOME, resposta.getUsuario().getNome())
                .putString(CHAVE_EMAIL, resposta.getUsuario().getEmail())
                .apply();
    }

    public String getToken() {
        return preferencias.getString(CHAVE_TOKEN, null);
    }

    public String getNome() {
        return preferencias.getString(CHAVE_NOME, "");
    }

    public String getEmail() {
        return preferencias.getString(CHAVE_EMAIL, "");
    }

    public boolean estaLogado() {
        return getToken() != null;
    }

    public void encerrar() {
        preferencias.edit().clear().apply();
    }
}
