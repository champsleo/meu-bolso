package com.meubolso.app.data.repository;

import android.content.Context;

import com.meubolso.app.data.model.AuthResponse;
import com.meubolso.app.data.model.CadastroRequest;
import com.meubolso.app.data.model.LoginRequest;
import com.meubolso.app.data.remote.ApiClient;
import com.meubolso.app.data.remote.ApiService;

/**
 * Repositório: única porta de entrada para os dados. As telas não sabem que existe Retrofit.
 */
public class AuthRepository {

    private final ApiService api;

    public AuthRepository(Context context) {
        this.api = ApiClient.getService(context);
    }

    public void login(LoginRequest request, ResultadoCallback<AuthResponse> callback) {
        api.login(request).enqueue(new ApiCallback<>(callback));
    }

    public void cadastrar(CadastroRequest request, ResultadoCallback<AuthResponse> callback) {
        api.cadastrar(request).enqueue(new ApiCallback<>(callback));
    }
}
