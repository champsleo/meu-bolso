package com.meubolso.app.ui.categorias;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.meubolso.app.R;
import com.meubolso.app.data.model.Categoria;
import com.meubolso.app.data.model.TipoTransacao;
import com.meubolso.app.databinding.ItemCategoriaBinding;
import com.meubolso.app.util.Formatadores;

import java.util.Objects;

public class CategoriaAdapter extends ListAdapter<Categoria, CategoriaAdapter.ViewHolder> {

    public interface Acoes {
        void editar(Categoria categoria);

        void excluir(Categoria categoria);
    }

    private static final DiffUtil.ItemCallback<Categoria> COMPARADOR = new DiffUtil.ItemCallback<Categoria>() {
        @Override
        public boolean areItemsTheSame(@NonNull Categoria antiga, @NonNull Categoria nova) {
            return Objects.equals(antiga.getId(), nova.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Categoria antiga, @NonNull Categoria nova) {
            return antiga.equals(nova);
        }
    };

    private final Acoes acoes;

    public CategoriaAdapter(Acoes acoes) {
        super(COMPARADOR);
        this.acoes = acoes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCategoriaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.exibir(getItem(position), acoes);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemCategoriaBinding binding;

        ViewHolder(ItemCategoriaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void exibir(Categoria categoria, Acoes acoes) {
            Context context = binding.getRoot().getContext();

            binding.indicadorCor.setBackgroundTintList(
                    ColorStateList.valueOf(Formatadores.cor(categoria.getCor())));
            binding.tvNome.setText(categoria.getNome());

            String tipo = context.getString(categoria.getTipo() == TipoTransacao.RECEITA
                    ? R.string.receita : R.string.despesa);
            if (categoria.getLimiteMensal() != null) {
                binding.tvDetalhes.setText(context.getString(R.string.tipo_com_limite,
                        tipo, Formatadores.moeda(categoria.getLimiteMensal())));
            } else {
                binding.tvDetalhes.setText(tipo);
            }

            binding.getRoot().setOnClickListener(v -> acoes.editar(categoria));
            binding.btnExcluir.setOnClickListener(v -> acoes.excluir(categoria));
        }
    }
}
