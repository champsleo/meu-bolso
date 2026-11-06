package com.meubolso.api.exception;

/** Gera resposta HTTP 409 (Conflict): por exemplo, e-mail já cadastrado. */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
