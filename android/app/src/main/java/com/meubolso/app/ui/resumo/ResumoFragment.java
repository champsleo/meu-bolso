package com.meubolso.app.ui.resumo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.meubolso.app.R;
import com.meubolso.app.data.model.GastoCategoria;
import com.meubolso.app.data.model.ResumoMensal;
import com.meubolso.app.data.repository.Resultado;
import com.meubolso.app.databinding.FragmentResumoBinding;
import com.meubolso.app.ui.main.MainViewModel;
import com.meubolso.app.util.Formatadores;

import java.util.Collections;
import java.util.List;

/**
 * Aba "Resumo": gráfico de despesas por categoria, limites mensais e totais do mês.
 */
public class ResumoFragment extends Fragment {

    private FragmentResumoBinding binding;
    private ResumoViewModel viewModel;
    private MainViewModel mainViewModel;
    private GastoCategoriaAdapter adapter;
    private boolean precisaRecarregar = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentResumoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ResumoViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        adapter = new GastoCategoriaAdapter();
        binding.listaGastos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listaGastos.setNestedScrollingEnabled(false);
        binding.listaGastos.setAdapter(adapter);

        binding.seletorMes.btnMesAnterior.setOnClickListener(v -> mainViewModel.mesAnterior());
        binding.seletorMes.btnProximoMes.setOnClickListener(v -> mainViewModel.proximoMes());

        viewModel.getResumo().observe(getViewLifecycleOwner(), this::exibir);
        mainViewModel.getMes().observe(getViewLifecycleOwner(), mes -> {
            binding.seletorMes.tvMes.setText(Formatadores.mesAno(mes));
            viewModel.carregar(mes);
        });
    }

    private void exibir(Resultado<ResumoMensal> resultado) {
        binding.progresso.setVisibility(resultado.isCarregando() ? View.VISIBLE : View.INVISIBLE);

        if (resultado.isErro()) {
            Snackbar.make(binding.getRoot(), resultado.getMensagem(), Snackbar.LENGTH_LONG)
                    .setAction(R.string.tentar_novamente, v -> viewModel.carregar(mainViewModel.mesAtual()))
                    .show();
            return;
        }
        if (!resultado.isSucesso() || resultado.getDados() == null) {
            return;
        }

        ResumoMensal resumo = resultado.getDados();
        List<GastoCategoria> gastos = resumo.getDespesasPorCategoria() != null
                ? resumo.getDespesasPorCategoria() : Collections.emptyList();

        binding.grafico.setDados(gastos, getString(R.string.total_gasto),
                Formatadores.moeda(resumo.getTotalDespesas()));
        adapter.submitList(gastos);
        binding.tvSemDespesas.setVisibility(gastos.isEmpty() ? View.VISIBLE : View.GONE);

        binding.tvTotalReceitas.setText(Formatadores.moeda(resumo.getTotalReceitas()));
        binding.tvTotalDespesas.setText(Formatadores.moeda(resumo.getTotalDespesas()));
        binding.tvSaldoFinal.setText(Formatadores.moeda(resumo.getSaldo()));
        binding.tvSaldoFinal.setTextColor(ContextCompat.getColor(requireContext(),
                resumo.getSaldo() != null && resumo.getSaldo().signum() < 0 ? R.color.despesa : R.color.receita));
        binding.tvQuantidade.setText(String.valueOf(resumo.getQuantidadeLancamentos()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (precisaRecarregar) {
            precisaRecarregar = false;
            viewModel.carregar(mainViewModel.mesAtual());
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
