package com.meubolso.app.data.repository;

/** Avisa quem chamou o repositório quando a resposta da API chegar. */
public interface ResultadoCallback<T> {

    void aoConcluir(Resultado<T> resultado);
}
