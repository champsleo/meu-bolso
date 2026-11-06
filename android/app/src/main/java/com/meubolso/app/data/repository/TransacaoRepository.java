package com.meubolso.app.data.repository;

import android.content.Context;

import com.meubolso.app.data.model.TipoTransacao;
import com.meubolso.app.data.model.Transacao;
import com.meubolso.app.data.model.TransacaoRequest;
import com.meubolso.app.data.remote.ApiClient;
import com.meubolso.app.data.remote.ApiService;

import java.time.YearMonth;
import java.util.List;

public class TransacaoRepository {

    private final ApiService api;

    public TransacaoRepository(Context context) {
        this.api = ApiClient.getService(context);
    }

    /** @param tipo filtro opcional (null = receitas e despesas) */
    public void listar(YearMonth mes, TipoTransacao tipo, ResultadoCallback<List<Transacao>> callback) {
        api.listarTransacoes(mes.toString(), tipo).enqueue(new ApiCallback<>(callback));
    }

    public void buscar(long id, ResultadoCallback<Transacao> callback) {
        api.buscarTransacao(id).enqueue(new ApiCallback<>(callback));
    }

    public void criar(TransacaoRequest request, ResultadoCallback<Transacao> callback) {
        api.criarTransacao(request).enqueue(new ApiCallback<>(callback));
    }

    public void atualizar(long id, TransacaoRequest request, ResultadoCallback<Transacao> callback) {
        api.atualizarTransacao(id, request).enqueue(new ApiCallback<>(callback));
    }

    public void excluir(long id, ResultadoCallback<Void> callback) {
        api.excluirTransacao(id).enqueue(new ApiCallback<>(callback));
    }
}
