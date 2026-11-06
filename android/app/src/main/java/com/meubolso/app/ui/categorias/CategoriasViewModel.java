package com.meubolso.app.ui.categorias;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.meubolso.app.R;
import com.meubolso.app.data.model.Categoria;
import com.meubolso.app.data.model.CategoriaRequest;
import com.meubolso.app.data.repository.CategoriaRepository;
import com.meubolso.app.data.repository.Resultado;
import com.meubolso.app.util.Evento;

import java.util.List;

/**
 * Compartilhado entre a lista de categorias e o diálogo de criar/editar categoria.
 */
public class CategoriasViewModel extends AndroidViewModel {

    private final CategoriaRepository repository;
    private final MutableLiveData<Resultado<List<Categoria>>> categorias = new MutableLiveData<>();
    private final MutableLiveData<Evento<String>> mensagem = new MutableLiveData<>();

    public CategoriasViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoriaRepository(application);
    }

    public LiveData<Resultado<List<Categoria>>> getCategorias() {
        return categorias;
    }

    /** Mensagens curtas para mostrar na tela (sucesso ou erro), entregues uma única vez. */
    public LiveData<Evento<String>> getMensagem() {
        return mensagem;
    }

    public void carregar() {
        categorias.setValue(Resultado.carregando());
        repository.listar(null, categorias::setValue);
    }

    /** @param id null para criar, ou o id da categoria para editar */
    public void salvar(@Nullable Long id, CategoriaRequest request) {
        if (id == null) {
            repository.criar(request, resultado -> aoConcluir(resultado, R.string.categoria_criada));
        } else {
            repository.atualizar(id, request, resultado -> aoConcluir(resultado, R.string.categoria_atualizada));
        }
    }

    public void excluir(Categoria categoria) {
        repository.excluir(categoria.getId(), resultado -> aoConcluir(resultado, R.string.categoria_excluida));
    }

    private void aoConcluir(Resultado<?> resultado, int mensagemSucesso) {
        if (resultado.isSucesso()) {
            mensagem.setValue(new Evento<>(getApplication().getString(mensagemSucesso)));
            carregar();
        } else if (resultado.isErro()) {
            mensagem.setValue(new Evento<>(resultado.getMensagem()));
        }
    }
}
