package com.meubolso.app.ui.inicio;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.meubolso.app.R;
import com.meubolso.app.data.model.TipoTransacao;
import com.meubolso.app.data.model.Transacao;
import com.meubolso.app.databinding.ItemTransacaoBinding;
import com.meubolso.app.util.Formatadores;

import java.util.Objects;

/**
 * Adapter da lista de lançamentos. ListAdapter + DiffUtil atualizam só os itens que mudaram.
 */
public class TransacaoAdapter extends ListAdapter<Transacao, TransacaoAdapter.ViewHolder> {

    public interface AoClicar {
        void aoClicar(Transacao transacao);
    }

    private static final DiffUtil.ItemCallback<Transacao> COMPARADOR = new DiffUtil.ItemCallback<Transacao>() {
        @Override
        public boolean areItemsTheSame(@NonNull Transacao antigo, @NonNull Transacao novo) {
            return Objects.equals(antigo.getId(), novo.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Transacao antigo, @NonNull Transacao novo) {
            return antigo.equals(novo);
        }
    };

    private final AoClicar aoClicar;

    public TransacaoAdapter(AoClicar aoClicar) {
        super(COMPARADOR);
        this.aoClicar = aoClicar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransacaoBinding binding = ItemTransacaoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.exibir(getItem(position), aoClicar);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemTransacaoBinding binding;

        ViewHolder(ItemTransacaoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void exibir(Transacao transacao, AoClicar aoClicar) {
            Context context = binding.getRoot().getContext();
            boolean receita = transacao.getTipo() == TipoTransacao.RECEITA;

            binding.tvDescricao.setText(transacao.getDescricao());
            binding.tvDetalhes.setText(context.getString(R.string.detalhes_lancamento,
                    transacao.getCategoriaNome(), Formatadores.data(transacao.getData())));

            String sinal = receita ? "+ " : "− ";
            binding.tvValor.setText(sinal + Formatadores.moeda(transacao.getValor()));
            binding.tvValor.setTextColor(ContextCompat.getColor(context,
                    receita ? R.color.receita : R.color.despesa));

            binding.indicadorCor.setBackgroundTintList(
                    ColorStateList.valueOf(Formatadores.cor(transacao.getCategoriaCor())));

            binding.getRoot().setOnClickListener(v -> aoClicar.aoClicar(transacao));
        }
    }
}
