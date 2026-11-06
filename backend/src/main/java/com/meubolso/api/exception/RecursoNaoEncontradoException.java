package com.meubolso.api.exception;

/** Gera resposta HTTP 404 (Not Found). */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
