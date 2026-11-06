package com.meubolso.app.data.model;

import java.util.List;

/** Formato de erro padronizado que o back-end devolve (ver ErroResponse no servidor). */
public class ErroApi {

    private int status;
    private String mensagem;
    private List<String> detalhes;

    public int getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public List<String> getDetalhes() {
        return detalhes;
    }
}
