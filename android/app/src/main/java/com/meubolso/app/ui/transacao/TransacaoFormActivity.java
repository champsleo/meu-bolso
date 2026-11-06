package com.meubolso.app.ui.transacao;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.meubolso.app.R;
import com.meubolso.app.data.model.Categoria;
import com.meubolso.app.data.model.TipoTransacao;
import com.meubolso.app.data.model.Transacao;
import com.meubolso.app.data.repository.Resultado;
import com.meubolso.app.databinding.ActivityTransacaoFormBinding;
import com.meubolso.app.util.Formatadores;
import com.meubolso.app.util.Telas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Formulário de lançamento. Sem EXTRA_ID cria um novo; com EXTRA_ID edita (e permite excluir).
 */
public class TransacaoFormActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "transacao_id";

    private ActivityTransacaoFormBinding binding;
    private TransacaoFormViewModel viewModel;
    private Long transacaoId;
    private final List<Categoria> todasCategorias = new ArrayList<>();
    /** Categoria do lançamento em edição que ainda espera a lista de categorias chegar. */
    private Long categoriaPendenteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Telas.ativarTelaCheia(this);
        binding = ActivityTransacaoFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Telas.aplicarEspacamento(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(TransacaoFormViewModel.class);
        transacaoId = getIntent().hasExtra(EXTRA_ID) ? getIntent().getLongExtra(EXTRA_ID, 0) : null;

        configurarToolbar();
        configurarCampos();

        viewModel.getCategorias().observe(this, this::exibirCategorias);
        viewModel.getTransacao().observe(this, this::exibirTransacao);
        viewModel.getOperacao().observe(this, this::exibirOperacao);

        viewModel.iniciar(transacaoId);
    }

    private void configurarToolbar() {
        binding.toolbar.setTitle(transacaoId == null ? R.string.novo_lancamento : R.string.editar_lancamento);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        if (transacaoId != null) {
            binding.toolbar.inflateMenu(R.menu.menu_transacao_form);
            binding.toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.acaoExcluir) {
                    confirmarExclusao();
                    return true;
                }
                return false;
            });
        }
    }

    private void configurarCampos() {
        atualizarCampoData();
        binding.etData.setOnClickListener(v -> abrirCalendario());

        binding.grupoTipo.addOnButtonCheckedListener((grupo, botaoId, marcado) -> {
            if (marcado) {
                // Trocou de Despesa para Receita (ou o contrário): a categoria escolhida deixa de valer
                Categoria selecionada = viewModel.getCategoriaSelecionada();
                if (selecionada != null && selecionada.getTipo() != tipoSelecionado()) {
                    viewModel.setCategoriaSelecionada(null);
                }
                atualizarListaDeCategorias();
            }
        });

        binding.dropCategoria.setOnItemClickListener((lista, view, posicao, id) ->
                viewModel.setCategoriaSelecionada((Categoria) lista.getItemAtPosition(posicao)));

        binding.btnSalvar.setOnClickListener(v -> viewModel.salvar(
                transacaoId,
                Telas.texto(binding.etDescricao),
                Formatadores.converterValor(Telas.texto(binding.etValor))));
    }

    private TipoTransacao tipoSelecionado() {
        return binding.grupoTipo.getCheckedButtonId() == R.id.btnReceita
                ? TipoTransacao.RECEITA : TipoTransacao.DESPESA;
    }

    // ---------- Categorias ----------

    private void exibirCategorias(Resultado<List<Categoria>> resultado) {
        if (resultado.isErro()) {
            Snackbar.make(binding.getRoot(), resultado.getMensagem(), Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!resultado.isSucesso() || resultado.getDados() == null) {
            return;
        }
        todasCategorias.clear();
        todasCategorias.addAll(resultado.getDados());
        atualizarListaDeCategorias();

        if (categoriaPendenteId != null) {
            selecionarCategoria(categoriaPendenteId);
        }
    }

    /** Mostra no dropdown apenas as categorias do tipo escolhido (receita ou despesa). */
    private void atualizarListaDeCategorias() {
        TipoTransacao tipo = tipoSelecionado();
        List<Categoria> filtradas = new ArrayList<>();
        for (Categoria categoria : todasCategorias) {
            if (categoria.getTipo() == tipo) {
                filtradas.add(categoria);
            }
        }
        binding.dropCategoria.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filtradas));

        Categoria selecionada = viewModel.getCategoriaSelecionada();
        boolean combina = selecionada != null && selecionada.getTipo() == tipo;
        binding.dropCategoria.setText(combina ? selecionada.getNome() : "", false);
    }

    private void selecionarCategoria(Long categoriaId) {
        if (todasCategorias.isEmpty()) {
            categoriaPendenteId = categoriaId; // a lista ainda não chegou da API
            return;
        }
        categoriaPendenteId = null;
        for (Categoria categoria : todasCategorias) {
            if (Objects.equals(categoria.getId(), categoriaId)) {
                viewModel.setCategoriaSelecionada(categoria);
                binding.dropCategoria.setText(categoria.getNome(), false);
                return;
            }
        }
    }

    // ---------- Modo edição ----------

    private void exibirTransacao(Resultado<Transacao> resultado) {
        binding.progresso.setVisibility(resultado.isCarregando() ? View.VISIBLE : View.GONE);

        if (resultado.isErro()) {
            Toast.makeText(this, resultado.getMensagem(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!resultado.isSucesso() || resultado.getDados() == null || viewModel.isFormularioPreenchido()) {
            return;
        }

        Transacao transacao = resultado.getDados();
        binding.grupoTipo.check(transacao.getTipo() == TipoTransacao.RECEITA ? R.id.btnReceita : R.id.btnDespesa);
        binding.etDescricao.setText(transacao.getDescricao());
        binding.etValor.setText(transacao.getValor().toPlainString());
        viewModel.setDataSelecionada(LocalDate.parse(transacao.getData()));
        atualizarCampoData();
        selecionarCategoria(transacao.getCategoriaId());
        viewModel.marcarFormularioPreenchido();
    }

    // ---------- Data ----------

    private void abrirCalendario() {
        LocalDate atual = viewModel.getDataSelecionada();
        new DatePickerDialog(this, (picker, ano, mes, dia) -> {
            viewModel.setDataSelecionada(LocalDate.of(ano, mes + 1, dia)); // no DatePicker o mês começa em 0
            atualizarCampoData();
        }, atual.getYear(), atual.getMonthValue() - 1, atual.getDayOfMonth()).show();
    }

    private void atualizarCampoData() {
        binding.etData.setText(Formatadores.data(viewModel.getDataSelecionada()));
    }

    // ---------- Salvar / excluir ----------

    private void exibirOperacao(Resultado<Boolean> resultado) {
        boolean carregando = resultado.isCarregando();
        binding.progresso.setVisibility(carregando ? View.VISIBLE : View.GONE);
        binding.btnSalvar.setEnabled(!carregando);

        if (resultado.isSucesso()) {
            boolean salvou = Boolean.TRUE.equals(resultado.getDados());
            Toast.makeText(this, salvou ? R.string.lancamento_salvo : R.string.lancamento_excluido,
                    Toast.LENGTH_SHORT).show();
            finish();
        } else if (resultado.isErro()) {
            Snackbar.make(binding.getRoot(), resultado.getMensagem(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void confirmarExclusao() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.excluir_lancamento)
                .setMessage(R.string.confirmar_exclusao_lancamento)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.excluir, (dialog, which) -> viewModel.excluir(transacaoId))
                .show();
    }
}
