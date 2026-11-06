package com.meubolso.app.util;

/**
 * Embrulha algo que deve acontecer UMA vez só (ex.: mostrar uma mensagem).
 * Sem isso, ao girar a tela o LiveData entregaria a mesma mensagem de novo.
 */
public class Evento<T> {

    private final T conteudo;
    private boolean tratado = false;

    public Evento(T conteudo) {
        this.conteudo = conteudo;
    }

    /** Retorna o conteúdo na primeira chamada e null nas seguintes. */
    public T consumir() {
        if (tratado) {
            return null;
        }
        tratado = true;
        return conteudo;
    }
}
