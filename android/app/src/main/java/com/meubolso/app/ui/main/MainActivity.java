package com.meubolso.app.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.meubolso.app.R;
import com.meubolso.app.databinding.ActivityMainBinding;
import com.meubolso.app.ui.auth.LoginActivity;
import com.meubolso.app.ui.categorias.CategoriasFragment;
import com.meubolso.app.ui.inicio.InicioFragment;
import com.meubolso.app.ui.resumo.ResumoFragment;
import com.meubolso.app.util.SessionManager;
import com.meubolso.app.util.Telas;

/**
 * Tela principal com menu inferior de 3 abas: Início, Resumo e Categorias.
 * Cada aba é um Fragment exibido dentro do "container".
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessao = new SessionManager(this);

        if (!sessao.estaLogado()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Telas.ativarTelaCheia(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Telas.aplicarEspacamento(binding.getRoot());

        configurarToolbar();

        binding.bottomNav.setOnItemSelectedListener(item -> {
            exibir(criarFragment(item.getItemId()));
            return true;
        });

        if (savedInstanceState == null) {
            exibir(new InicioFragment());
        }
    }

    private void configurarToolbar() {
        binding.toolbar.setSubtitle(getString(R.string.ola_usuario, primeiroNome(sessao.getNome())));
        binding.toolbar.inflateMenu(R.menu.menu_main);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.acaoSair) {
                confirmarSaida();
                return true;
            }
            return false;
        });
    }

    private Fragment criarFragment(@IdRes int itemId) {
        if (itemId == R.id.navResumo) {
            return new ResumoFragment();
        } else if (itemId == R.id.navCategorias) {
            return new CategoriasFragment();
        }
        return new InicioFragment();
    }

    private void exibir(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private void confirmarSaida() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.sair)
                .setMessage(R.string.confirmar_saida)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.sair, (dialog, which) -> {
                    sessao.encerrar();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    private String primeiroNome(String nomeCompleto) {
        String nome = nomeCompleto == null ? "" : nomeCompleto.trim();
        int espaco = nome.indexOf(' ');
        return espaco > 0 ? nome.substring(0, espaco) : nome;
    }
}
