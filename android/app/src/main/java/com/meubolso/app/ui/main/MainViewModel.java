package com.meubolso.app.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.YearMonth;

/**
 * Compartilhado entre as abas da tela principal: guarda o mês que está sendo visualizado,
 * para que "Início" e "Resumo" mostrem sempre o mesmo mês.
 */
public class MainViewModel extends ViewModel {

    private final MutableLiveData<YearMonth> mes = new MutableLiveData<>(YearMonth.now());

    public LiveData<YearMonth> getMes() {
        return mes;
    }

    public YearMonth mesAtual() {
        YearMonth valor = mes.getValue();
        return valor != null ? valor : YearMonth.now();
    }

    public void mesAnterior() {
        mes.setValue(mesAtual().minusMonths(1));
    }

    public void proximoMes() {
        mes.setValue(mesAtual().plusMonths(1));
    }
}
