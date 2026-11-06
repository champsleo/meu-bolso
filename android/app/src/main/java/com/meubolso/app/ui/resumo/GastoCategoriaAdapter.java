package com.meubolso.app.ui.resumo;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.meubolso.app.R;
import com.meubolso.app.data.model.GastoCategoria;
import com.meubolso.app.databinding.ItemGastoCategoriaBinding;
import com.meubolso.app.util.Formatadores;

import java.util.Objects;

/**
 * Lista "gasto por categoria", com barra de progresso quando a categoria tem limite mensal.
 */
public class GastoCategoriaAdapter extends ListAdapter<GastoCategoria, GastoCategoriaAdapter.ViewHolder> {

    /** A partir de 80% do limite a barra fica laranja, como um aviso. */
    private static final double ALERTA_LIMITE = 80.0;

    private static final DiffUtil.ItemCallback<GastoCategoria> COMPARADOR =
            new DiffUtil.ItemCallback<GastoCategoria>() {
                @Override
                public boolean areItemsTheSame(@NonNull GastoCategoria antigo, @NonNull GastoCategoria novo) {
                    return Objects.equals(antigo.getCategoriaId(), novo.getCategoriaId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull GastoCategoria antigo, @NonNull GastoCategoria novo) {
                    return Objects.equals(antigo.getNome(), novo.getNome())
                            && Objects.equals(antigo.getCor(), novo.getCor())
                            && Objects.equals(antigo.getTotal(), novo.getTotal())
                            && Objects.equals(antigo.getLimiteMensal(), novo.getLimiteMensal())
                            && antigo.getPercentualDoTotal() == novo.getPercentualDoTotal();
                }
            };

    public GastoCategoriaAdapter() {
        super(COMPARADOR);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemGastoCategoriaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.exibir(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemGastoCategoriaBinding binding;

        ViewHolder(ItemGastoCategoriaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void exibir(GastoCategoria gasto) {
            Context context = binding.getRoot().getContext();

            binding.indicadorCor.setBackgroundTintList(
                    ColorStateList.valueOf(Formatadores.cor(gasto.getCor())));
            binding.tvNome.setText(gasto.getNome());
            binding.tvTotal.setText(Formatadores.moeda(gasto.getTotal()));
            binding.tvPercentual.setText(Formatadores.percentual(gasto.getPercentualDoTotal()));

            if (gasto.getLimiteMensal() == null || gasto.getPercentualDoLimite() == null) {
                binding.barraLimite.setVisibility(View.GONE);
                binding.tvLimite.setVisibility(View.GONE);
                return;
            }

            double usado = gasto.getPercentualDoLimite();
            int corBarra;
            if (gasto.isLimiteExcedido()) {
                corBarra = ContextCompat.getColor(context, R.color.despesa);
            } else if (usado >= ALERTA_LIMITE) {
                corBarra = ContextCompat.getColor(context, R.color.alerta);
            } else {
                corBarra = ContextCompat.getColor(context, R.color.receita);
            }

            binding.barraLimite.setVisibility(View.VISIBLE);
            binding.barraLimite.setIndicatorColor(corBarra);
            binding.barraLimite.setProgressCompat((int) Math.min(100, Math.round(usado)), false);

            int textoId = gasto.isLimiteExcedido() ? R.string.limite_excedido : R.string.limite_usado;
            binding.tvLimite.setVisibility(View.VISIBLE);
            binding.tvLimite.setText(context.getString(textoId,
                    Formatadores.moeda(gasto.getTotal()),
                    Formatadores.moeda(gasto.getLimiteMensal()),
                    Formatadores.percentual(usado)));
            binding.tvLimite.setTextColor(gasto.isLimiteExcedido()
                    ? corBarra
                    : ContextCompat.getColor(context, R.color.texto_secundario));
        }
    }
}
