package com.meubolso.api.exception;

/** Gera resposta HTTP 422 (Unprocessable Entity): os dados são válidos, mas violam uma regra do sistema. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
