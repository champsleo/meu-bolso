package com.meubolso.app.data.repository;

import android.content.Context;

import com.meubolso.app.data.model.Categoria;
import com.meubolso.app.data.model.CategoriaRequest;
import com.meubolso.app.data.model.TipoTransacao;
import com.meubolso.app.data.remote.ApiClient;
import com.meubolso.app.data.remote.ApiService;

import java.util.List;

public class CategoriaRepository {

    private final ApiService api;

    public CategoriaRepository(Context context) {
        this.api = ApiClient.getService(context);
    }

    /** @param tipo filtro opcional (null = todas) */
    public void listar(TipoTransacao tipo, ResultadoCallback<List<Categoria>> callback) {
        api.listarCategorias(tipo).enqueue(new ApiCallback<>(callback));
    }

    public void criar(CategoriaRequest request, ResultadoCallback<Categoria> callback) {
        api.criarCategoria(request).enqueue(new ApiCallback<>(callback));
    }

    public void atualizar(long id, CategoriaRequest request, ResultadoCallback<Categoria> callback) {
        api.atualizarCategoria(id, request).enqueue(new ApiCallback<>(callback));
    }

    public void excluir(long id, ResultadoCallback<Void> callback) {
        api.excluirCategoria(id).enqueue(new ApiCallback<>(callback));
    }
}
