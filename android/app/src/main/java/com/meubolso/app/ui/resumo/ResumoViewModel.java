package com.meubolso.app.ui.resumo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.meubolso.app.data.model.ResumoMensal;
import com.meubolso.app.data.repository.Resultado;
import com.meubolso.app.data.repository.ResumoRepository;

import java.time.YearMonth;

public class ResumoViewModel extends AndroidViewModel {

    private final ResumoRepository repository;
    private final MutableLiveData<Resultado<ResumoMensal>> resumo = new MutableLiveData<>();
    private int requisicaoAtual = 0;

    public ResumoViewModel(@NonNull Application application) {
        super(application);
        repository = new ResumoRepository(application);
    }

    public LiveData<Resultado<ResumoMensal>> getResumo() {
        return resumo;
    }

    public void carregar(YearMonth mes) {
        final int requisicao = ++requisicaoAtual;
        resumo.setValue(Resultado.carregando());
        repository.buscar(mes, resultado -> {
            if (requisicao == requisicaoAtual) {
                resumo.setValue(resultado);
            }
        });
    }
}
