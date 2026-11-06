package com.meubolso.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.meubolso.app.R;
import com.meubolso.app.data.model.AuthResponse;
import com.meubolso.app.data.repository.Resultado;
import com.meubolso.app.databinding.ActivityLoginBinding;
import com.meubolso.app.ui.main.MainActivity;
import com.meubolso.app.util.SessionManager;
import com.meubolso.app.util.Telas;

/**
 * Primeira tela do app. Se o usuário já estiver logado, vai direto para a tela principal.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_SESSAO_EXPIRADA = "sessao_expirada";

    private static final String EMAIL_DEMO = "demo@meubolso.com";
    private static final String SENHA_DEMO = "123456";

    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (new SessionManager(this).estaLogado()) {
            abrirTelaPrincipal();
            return;
        }

        Telas.ativarTelaCheia(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Telas.aplicarEspacamento(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        if (savedInstanceState == null && getIntent().getBooleanExtra(EXTRA_SESSAO_EXPIRADA, false)) {
            Toast.makeText(this, R.string.sessao_expirada, Toast.LENGTH_LONG).show();
        }

        configurarAcoes();
        viewModel.getResultado().observe(this, this::exibirResultado);
    }

    private void configurarAcoes() {
        binding.btnEntrar.setOnClickListener(v -> entrar());

        binding.etSenha.setOnEditorActionListener((campo, acao, evento) -> {
            if (acao == EditorInfo.IME_ACTION_DONE) {
                entrar();
                return true;
            }
            return false;
        });

        binding.btnCriarConta.setOnClickListener(v ->
                startActivity(new Intent(this, CadastroActivity.class)));

        binding.btnContaDemo.setOnClickListener(v -> {
            binding.etEmail.setText(EMAIL_DEMO);
            binding.etSenha.setText(SENHA_DEMO);
        });
    }

    private void entrar() {
        viewModel.login(Telas.texto(binding.etEmail), Telas.texto(binding.etSenha));
    }

    private void exibirResultado(Resultado<AuthResponse> resultado) {
        boolean carregando = resultado.isCarregando();
        binding.progresso.setVisibility(carregando ? View.VISIBLE : View.GONE);
        binding.btnEntrar.setEnabled(!carregando);

        if (resultado.isSucesso()) {
            abrirTelaPrincipal();
        } else if (resultado.isErro()) {
            Snackbar.make(binding.getRoot(), resultado.getMensagem(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void abrirTelaPrincipal() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
