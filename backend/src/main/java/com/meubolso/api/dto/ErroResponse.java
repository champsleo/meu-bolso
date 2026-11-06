package com.meubolso.api.dto;

import java.time.Instant;
import java.util.List;

/**
 * Formato padrão de todos os erros da API. O app Android exibe o campo "mensagem".
 */
public record ErroResponse(
        int status,
        String erro,
        String mensagem,
        List<String> detalhes,
        Instant timestamp) {

    public static ErroResponse de(int status, String erro, String mensagem, List<String> detalhes) {
        return new ErroResponse(status, erro, mensagem, detalhes, Instant.now());
    }
}
