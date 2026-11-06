package com.meubolso.app.ui.inicio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.meubolso.app.R;
import com.meubolso.app.data.model.ResumoMensal;
import com.meubolso.app.data.model.TipoTransacao;
import com.meubolso.app.data.model.Transacao;
import com.meubolso.app.data.repository.Resultado;
import com.meubolso.app.databinding.FragmentInicioBinding;
import com.meubolso.app.ui.main.MainViewModel;
import com.meubolso.app.ui.transacao.TransacaoFormActivity;
import com.meubolso.app.util.Formatadores;

import java.util.Collections;
import java.util.List;

/**
 * Aba "Início": saldo do mês, filtro por tipo e lista de lançamentos.
 */
public class InicioFragment extends Fragment {

    private FragmentInicioBinding binding;
    private InicioViewModel viewModel;
    private MainViewModel mainViewModel;
    private TransacaoAdapter adapter;
    private boolean precisaRecarregar = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInicioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(InicioViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        configurarLista();
        configurarAcoes();

        viewModel.getTransacoes().observe(getViewLifecycleOwner(), this::exibirTransacoes);
        viewModel.getResumo().observe(getViewLifecycleOwner(), this::exibirResumo);
        mainViewModel.getMes().observe(getViewLifecycleOwner(), mes -> {
            binding.seletorMes.tvMes.setText(Formatadores.mesAno(mes));
            carregar();
        });
    }

    private void configurarLista() {
        adapter = new TransacaoAdapter(transacao -> abrirFormulario(transacao.getId()));
        binding.listaTransacoes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listaTransacoes.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        binding.listaTransacoes.setAdapter(adapter);
    }

    private void configurarAcoes() {
        binding.seletorMes.btnMesAnterior.setOnClickListener(v -> mainViewModel.mesAnterior());
        binding.seletorMes.btnProximoMes.setOnClickListener(v -> mainViewModel.proximoMes());
        binding.swipeRefresh.setOnRefreshListener(this::carregar);
        binding.fabNovo.setOnClickListener(v -> abrirFormulario(null));
        binding.chipsFiltro.setOnCheckedStateChangeListener((grupo, selecionados) -> {
            viewModel.setFiltro(filtroSelecionado());
            carregar();
        });
    }

    private TipoTransacao filtroSelecionado() {
        int selecionado = binding.chipsFiltro.getCheckedChipId();
        if (selecionado == R.id.chipReceitas) {
            return TipoTransacao.RECEITA;
        } else if (selecionado == R.id.chipDespesas) {
            return TipoTransacao.DESPESA;
        }
        return null;
    }

    private void carregar() {
        viewModel.carregar(mainViewModel.mesAtual());
    }

    private void exibirTransacoes(Resultado<List<Transacao>> resultado) {
        binding.swipeRefresh.setRefreshing(resultado.isCarregando());

        if (resultado.isSucesso()) {
            List<Transacao> lista = resultado.getDados() != null
                    ? resultado.getDados() : Collections.emptyList();
            adapter.submitList(lista);
            binding.tvVazio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        } else if (resultado.isErro()) {
            Snackbar.make(binding.getRoot(), resultado.getMensagem(), Snackbar.LENGTH_LONG)
                    .setAction(R.string.tentar_novamente, v -> carregar())
                    .show();
        }
    }

    private void exibirResumo(Resultado<ResumoMensal> resultado) {
        if (!resultado.isSucesso() || resultado.getDados() == null) {
            return;
        }
        ResumoMensal resumo = resultado.getDados();
        binding.tvSaldo.setText(Formatadores.moeda(resumo.getSaldo()));
        binding.tvReceitas.setText(Formatadores.moeda(resumo.getTotalReceitas()));
        binding.tvDespesas.setText(Formatadores.moeda(resumo.getTotalDespesas()));
    }

    private void abrirFormulario(@Nullable Long transacaoId) {
        Intent intent = new Intent(requireContext(), TransacaoFormActivity.class);
        if (transacaoId != null) {
            intent.putExtra(TransacaoFormActivity.EXTRA_ID, transacaoId.longValue());
        }
        startActivity(intent);
    }

    /** Ao voltar do formulário, recarrega para mostrar o que foi criado/editado. */
    @Override
    public void onResume() {
        super.onResume();
        if (precisaRecarregar) {
            precisaRecarregar = false;
            carregar();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        precisaRecarregar = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
