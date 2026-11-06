package com.meubolso.api.exception;

/** Gera resposta HTTP 401 (Unauthorized): e-mail ou senha incorretos. */
public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException() {
        super("E-mail ou senha incorretos");
    }
}
