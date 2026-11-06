package com.meubolso.app.ui.categorias;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.meubolso.app.R;
import com.meubolso.app.data.model.Categoria;
import com.meubolso.app.data.model.CategoriaRequest;
import com.meubolso.app.data.model.TipoTransacao;
import com.meubolso.app.databinding.DialogCategoriaBinding;
import com.meubolso.app.util.Formatadores;
import com.meubolso.app.util.Telas;

import java.math.BigDecimal;

/**
 * Diálogo para criar ou editar uma categoria (nome, tipo, cor e limite mensal).
 */
public class CategoriaDialogFragment extends DialogFragment {

    private static final String ARG_CATEGORIA = "categoria";
    private static final String ESTADO_COR = "cor";

    private static final String[] CORES = {
            "#EF5350", "#EC407A", "#AB47BC", "#7E57C2", "#42A5F5",
            "#26A69A", "#66BB6A", "#FFA726", "#8D6E63", "#78909C"
    };

    private DialogCategoriaBinding binding;
    private Categoria categoria;
    private String corSelecionada = CORES[0];

    public static CategoriaDialogFragment novaInstancia(@Nullable Categoria categoria) {
        Bundle argumentos = new Bundle();
        argumentos.putSerializable(ARG_CATEGORIA, categoria);
        CategoriaDialogFragment dialogo = new CategoriaDialogFragment();
        dialogo.setArguments(argumentos);
        return dialogo;
    }

    @SuppressWarnings("deprecation") // getSerializable(String) funciona em todas as versões
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        categoria = (Categoria) requireArguments().getSerializable(ARG_CATEGORIA);
        // Importante: em DialogFragment use LayoutInflater.from(...) e não getLayoutInflater()
        binding = DialogCategoriaBinding.inflate(LayoutInflater.from(requireContext()));

        if (categoria != null) {
            preencher(categoria);
        }
        if (savedInstanceState != null) {
            corSelecionada = savedInstanceState.getString(ESTADO_COR, corSelecionada);
        }

        montarPaletaDeCores();
        atualizarCampoLimite();
        binding.grupoTipo.addOnButtonCheckedListener((grupo, botaoId, marcado) -> {
            if (marcado) {
                atualizarCampoLimite();
            }
        });

        AlertDialog dialogo = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(categoria == null ? R.string.nova_categoria : R.string.editar_categoria)
                .setView(binding.getRoot())
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.salvar, null) // definido abaixo para não fechar se houver erro
                .create();

        dialogo.setOnShowListener(d ->
                dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> salvar()));
        return dialogo;
    }

    private void preencher(Categoria existente) {
        binding.etNome.setText(existente.getNome());
        binding.grupoTipo.check(existente.getTipo() == TipoTransacao.RECEITA
                ? R.id.btnReceita : R.id.btnDespesa);
        if (existente.getCor() != null) {
            corSelecionada = existente.getCor();
        }
        if (existente.getLimiteMensal() != null) {
            binding.etLimite.setText(existente.getLimiteMensal().toPlainString());
        }
    }

    private TipoTransacao tipoSelecionado() {
        return binding.grupoTipo.getCheckedButtonId() == R.id.btnReceita
                ? TipoTransacao.RECEITA : TipoTransacao.DESPESA;
    }

    /** Limite mensal só existe para despesas. */
    private void atualizarCampoLimite() {
        boolean despesa = tipoSelecionado() == TipoTransacao.DESPESA;
        binding.campoLimite.setVisibility(despesa ? View.VISIBLE : View.GONE);
    }

    private void montarPaletaDeCores() {
        int tamanho = getResources().getDimensionPixelSize(R.dimen.tamanho_cor);
        int margem = getResources().getDimensionPixelSize(R.dimen.margem_cor);

        for (String cor : CORES) {
            View bolinha = new View(requireContext());
            LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(tamanho, tamanho);
            parametros.setMargins(margem, margem, margem, margem);
            bolinha.setLayoutParams(parametros);
            bolinha.setBackgroundResource(R.drawable.circulo);
            bolinha.setBackgroundTintList(ColorStateList.valueOf(Formatadores.cor(cor)));
            bolinha.setTag(cor);
            bolinha.setContentDescription(cor);
            bolinha.setOnClickListener(v -> {
                corSelecionada = (String) v.getTag();
                destacarCorSelecionada();
            });
            binding.paletaCores.addView(bolinha);
        }
        destacarCorSelecionada();
    }

    private void destacarCorSelecionada() {
        for (int i = 0; i < binding.paletaCores.getChildCount(); i++) {
            View bolinha = binding.paletaCores.getChildAt(i);
            boolean selecionada = corSelecionada.equalsIgnoreCase((String) bolinha.getTag());
            float escala = selecionada ? 1.0f : 0.7f;
            bolinha.setScaleX(escala);
            bolinha.setScaleY(escala);
            bolinha.setAlpha(selecionada ? 1.0f : 0.6f);
        }
    }

    private void salvar() {
        binding.campoNome.setError(null);
        binding.campoLimite.setError(null);

        String nome = Telas.texto(binding.etNome);
        if (nome.isEmpty()) {
            binding.campoNome.setError(getString(R.string.erro_informe_nome_categoria));
            return;
        }

        TipoTransacao tipo = tipoSelecionado();
        BigDecimal limite = null;
        String textoLimite = Telas.texto(binding.etLimite);
        if (tipo == TipoTransacao.DESPESA && !textoLimite.isEmpty()) {
            limite = Formatadores.converterValor(textoLimite);
            if (limite == null || limite.signum() <= 0) {
                binding.campoLimite.setError(getString(R.string.erro_valor_invalido));
                return;
            }
        }

        CategoriasViewModel viewModel =
                new ViewModelProvider(requireParentFragment()).get(CategoriasViewModel.class);
        viewModel.salvar(categoria == null ? null : categoria.getId(),
                new CategoriaRequest(nome, tipo, corSelecionada, limite));
        dismiss();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ESTADO_COR, corSelecionada);
    }
}
