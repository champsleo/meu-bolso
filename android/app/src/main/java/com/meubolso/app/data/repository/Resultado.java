package com.meubolso.app.data.repository;

/**
 * Envelope que representa o estado de uma chamada à API:
 * CARREGANDO (mostrar progresso), SUCESSO (com dados) ou ERRO (com mensagem).
 */
public class Resultado<T> {

    public enum Status { CARREGANDO, SUCESSO, ERRO }

    private final Status status;
    private final T dados;
    private final String mensagem;

    private Resultado(Status status, T dados, String mensagem) {
        this.status = status;
        this.dados = dados;
        this.mensagem = mensagem;
    }

    public static <T> Resultado<T> carregando() {
        return new Resultado<>(Status.CARREGANDO, null, null);
    }

    public static <T> Resultado<T> sucesso(T dados) {
        return new Resultado<>(Status.SUCESSO, dados, null);
    }

    public static <T> Resultado<T> erro(String mensagem) {
        return new Resultado<>(Status.ERRO, null, mensagem);
    }

    public Status getStatus() {
        return status;
    }

    public T getDados() {
        return dados;
    }

    public String getMensagem() {
        return mensagem;
    }

    public boolean isCarregando() {
        return status == Status.CARREGANDO;
    }

    public boolean isSucesso() {
        return status == Status.SUCESSO;
    }

    public boolean isErro() {
        return status == Status.ERRO;
    }
}
