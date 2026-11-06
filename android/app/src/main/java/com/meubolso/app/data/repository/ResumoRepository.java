package com.meubolso.app.data.repository;

import android.content.Context;

import com.meubolso.app.data.model.ResumoMensal;
import com.meubolso.app.data.remote.ApiClient;
import com.meubolso.app.data.remote.ApiService;

import java.time.YearMonth;

public class ResumoRepository {

    private final ApiService api;

    public ResumoRepository(Context context) {
        this.api = ApiClient.getService(context);
    }

    public void buscar(YearMonth mes, ResultadoCallback<ResumoMensal> callback) {
        api.buscarResumo(mes.toString()).enqueue(new ApiCallback<>(callback));
    }
}
