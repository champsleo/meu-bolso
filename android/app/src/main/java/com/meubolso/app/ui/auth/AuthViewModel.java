package com.meubolso.app.ui.auth;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.meubolso.app.R;
import com.meubolso.app.data.model.AuthResponse;
import com.meubolso.app.data.model.CadastroRequest;
import com.meubolso.app.data.model.LoginRequest;
import com.meubolso.app.data.repository.AuthRepository;
import com.meubolso.app.data.repository.Resultado;
import com.meubolso.app.util.SessionManager;

/**
 * ViewModel das telas de login e cadastro.
 * Faz a validação dos campos e conversa com o repositório; a Activity só exibe o resultado.
 * Ele sobrevive à rotação de tela, então a requisição não é perdida se o usuário girar o celular.
 */
public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repository;
    private final SessionManager sessao;
    private final MutableLiveData<Resultado<AuthResponse>> resultado = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.repository = new AuthRepository(application);
        this.sessao = new SessionManager(application);
    }

    public LiveData<Resultado<AuthResponse>> getResultado() {
        return resultado;
    }

    public void login(String email, String senha) {
        if (email.isEmpty() || senha.isEmpty()) {
            resultado.setValue(Resultado.erro(texto(R.string.erro_preencha_email_senha)));
            return;
        }
        resultado.setValue(Resultado.carregando());
        repository.login(new LoginRequest(email, senha), this::aoAutenticar);
    }

    public void cadastrar(String nome, String email, String senha, String confirmacao) {
        String erro = validarCadastro(nome, email, senha, confirmacao);
        if (erro != null) {
            resultado.setValue(Resultado.erro(erro));
            return;
        }
        resultado.setValue(Resultado.carregando());
        repository.cadastrar(new CadastroRequest(nome, email, senha), this::aoAutenticar);
    }

    private void aoAutenticar(Resultado<AuthResponse> resposta) {
        if (resposta.isSucesso() && resposta.getDados() != null) {
            sessao.salvar(resposta.getDados());
        }
        resultado.setValue(resposta);
    }

    private String validarCadastro(String nome, String email, String senha, String confirmacao) {
        if (nome.isEmpty()) {
            return texto(R.string.erro_informe_nome);
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return texto(R.string.erro_email_invalido);
        }
        if (senha.length() < 6) {
            return texto(R.string.erro_senha_curta);
        }
        if (!senha.equals(confirmacao)) {
            return texto(R.string.erro_senhas_diferentes);
        }
        return null;
    }

    private String texto(@StringRes int id) {
        return getApplication().getString(id);
    }
}
