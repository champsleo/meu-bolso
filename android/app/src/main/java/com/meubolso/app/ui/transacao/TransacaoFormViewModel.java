package com.meubolso.app.ui.transacao;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.meubolso.app.R;
import com.meubolso.app.data.model.Categoria;
import com.meubolso.app.data.model.Transacao;
import com.meubolso.app.data.model.TransacaoRequest;
import com.meubolso.app.data.repository.CategoriaRepository;
import com.meubolso.app.data.repository.Resultado;
import com.meubolso.app.data.repository.ResultadoCallback;
import com.meubolso.app.data.repository.TransacaoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * ViewModel do formulário de lançamento (criar, editar e excluir).
 * Guarda a data e a categoria escolhidas, para não perdê-las ao girar a tela.
 */
public class TransacaoFormViewModel extends AndroidViewModel {

    private final CategoriaRepository categoriaRepository;
    private final TransacaoRepository transacaoRepository;

    private final MutableLiveData<Resultado<List<Categoria>>> categorias = new MutableLiveData<>();
    private final MutableLiveData<Resultado<Transacao>> transacao = new MutableLiveData<>();
    private final MutableLiveData<Resultado<Boolean>> operacao = new MutableLiveData<>();

    private LocalDate dataSelecionada = LocalDate.now();
    private Categoria categoriaSelecionada;
    private boolean iniciado = false;
    private boolean formularioPreenchido = false;

    public TransacaoFormViewModel(@NonNull Application application) {
        super(application);
        categoriaRepository = new CategoriaRepository(application);
        transacaoRepository = new TransacaoRepository(application);
    }

    public LiveData<Resultado<List<Categoria>>> getCategorias() {
        return categorias;
    }

    /** Lançamento carregado da API (somente no modo edição). */
    public LiveData<Resultado<Transacao>> getTransacao() {
        return transacao;
    }

    /** Resultado de salvar ou excluir. */
    public LiveData<Resultado<Boolean>> getOperacao() {
        return operacao;
    }

    /** Carrega os dados uma única vez (mesmo que a tela seja recriada). */
    public void iniciar(@Nullable Long transacaoId) {
        if (iniciado) {
            return;
        }
        iniciado = true;
        categorias.setValue(Resultado.carregando());
        categoriaRepository.listar(null, categorias::setValue);

        if (transacaoId != null) {
            transacao.setValue(Resultado.carregando());
            transacaoRepository.buscar(transacaoId, transacao::setValue);
        }
    }

    public LocalDate getDataSelecionada() {
        return dataSelecionada;
    }

    public void setDataSelecionada(LocalDate data) {
        this.dataSelecionada = data;
    }

    public Categoria getCategoriaSelecionada() {
        return categoriaSelecionada;
    }

    public void setCategoriaSelecionada(@Nullable Categoria categoria) {
        this.categoriaSelecionada = categoria;
    }

    public boolean isFormularioPreenchido() {
        return formularioPreenchido;
    }

    public void marcarFormularioPreenchido() {
        this.formularioPreenchido = true;
    }

    public void salvar(@Nullable Long transacaoId, String descricao, BigDecimal valor) {
        if (descricao.isEmpty()) {
            erro(R.string.erro_informe_descricao);
            return;
        }
        if (valor == null || valor.signum() <= 0) {
            erro(R.string.erro_valor_invalido);
            return;
        }
        if (categoriaSelecionada == null) {
            erro(R.string.erro_selecione_categoria);
            return;
        }

        TransacaoRequest request = new TransacaoRequest(
                descricao, valor, dataSelecionada.toString(), categoriaSelecionada.getId());

        operacao.setValue(Resultado.carregando());
        ResultadoCallback<Transacao> aoSalvar = resultado -> operacao.setValue(resultado.isSucesso()
                ? Resultado.sucesso(true) : Resultado.erro(resultado.getMensagem()));

        if (transacaoId == null) {
            transacaoRepository.criar(request, aoSalvar);
        } else {
            transacaoRepository.atualizar(transacaoId, request, aoSalvar);
        }
    }

    public void excluir(long transacaoId) {
        operacao.setValue(Resultado.carregando());
        transacaoRepository.excluir(transacaoId, resultado -> operacao.setValue(resultado.isSucesso()
                ? Resultado.sucesso(false) : Resultado.erro(resultado.getMensagem())));
    }

    private void erro(@StringRes int mensagem) {
        operacao.setValue(Resultado.erro(getApplication().getString(mensagem)));
    }
}
