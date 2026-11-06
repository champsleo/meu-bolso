package com.meubolso.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.meubolso.app.data.model.AuthResponse;
import com.meubolso.app.data.repository.Resultado;
import com.meubolso.app.databinding.ActivityCadastroBinding;
import com.meubolso.app.ui.main.MainActivity;
import com.meubolso.app.util.Telas;

/**
 * Tela de criação de conta. Após o cadastro o usuário já entra logado.
 */
public class CadastroActivity extends AppCompatActivity {

    private ActivityCadastroBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Telas.ativarTelaCheia(this);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Telas.aplicarEspacamento(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.btnCadastrar.setOnClickListener(v -> viewModel.cadastrar(
                Telas.texto(binding.etNome),
                Telas.texto(binding.etEmail),
                Telas.texto(binding.etSenha),
                Telas.texto(binding.etConfirmarSenha)));

        viewModel.getResultado().observe(this, this::exibirResultado);
    }

    private void exibirResultado(Resultado<AuthResponse> resultado) {
        boolean carregando = resultado.isCarregando();
        binding.progresso.setVisibility(carregando ? View.VISIBLE : View.GONE);
        binding.btnCadastrar.setEnabled(!carregando);

        if (resultado.isSucesso()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (resultado.isErro()) {
            Snackbar.make(binding.getRoot(), resultado.getMensagem(), Snackbar.LENGTH_LONG).show();
        }
    }
}
