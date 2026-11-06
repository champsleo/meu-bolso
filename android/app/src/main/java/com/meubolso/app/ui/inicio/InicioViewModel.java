package com.meubolso.app.ui.inicio;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.meubolso.app.data.model.ResumoMensal;
import com.meubolso.app.data.model.TipoTransacao;
import com.meubolso.app.data.model.Transacao;
import com.meubolso.app.data.repository.Resultado;
import com.meubolso.app.data.repository.ResumoRepository;
import com.meubolso.app.data.repository.TransacaoRepository;

import java.time.YearMonth;
import java.util.List;

/**
 * Carrega os lançamentos e os totais do mês para a aba "Início".
 */
public class InicioViewModel extends AndroidViewModel {

    private final TransacaoRepository transacaoRepository;
    private final ResumoRepository resumoRepository;

    private final MutableLiveData<Resultado<List<Transacao>>> transacoes = new MutableLiveData<>();
    private final MutableLiveData<Resultado<ResumoMensal>> resumo = new MutableLiveData<>();

    private TipoTransacao filtro = null;
    /** Identifica a última requisição, para ignorar respostas antigas se o usuário trocar de mês rápido. */
    private int requisicaoAtual = 0;

    public InicioViewModel(@NonNull Application application) {
        super(application);
        transacaoRepository = new TransacaoRepository(application);
        resumoRepository = new ResumoRepository(application);
    }

    public LiveData<Resultado<List<Transacao>>> getTransacoes() {
        return transacoes;
    }

    public LiveData<Resultado<ResumoMensal>> getResumo() {
        return resumo;
    }

    public void setFiltro(TipoTransacao filtro) {
        this.filtro = filtro;
    }

    public void carregar(YearMonth mes) {
        final int requisicao = ++requisicaoAtual;
        transacoes.setValue(Resultado.carregando());

        transacaoRepository.listar(mes, filtro, resultado -> {
            if (requisicao == requisicaoAtual) {
                transacoes.setValue(resultado);
            }
        });
        resumoRepository.buscar(mes, resultado -> {
            if (requisicao == requisicaoAtual) {
                resumo.setValue(resultado);
            }
        });
    }
}
