package com.meubolso.app.util;

import android.graphics.Color;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Funções utilitárias para exibir dinheiro, datas e cores no padrão brasileiro.
 */
public final class Formatadores {

    public static final Locale BRASIL = Locale.forLanguageTag("pt-BR");

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy", BRASIL);
    private static final DateTimeFormatter MES_ANO = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", BRASIL);

    private Formatadores() {
    }

    /** 1234.5 → "R$ 1.234,50" */
    public static String moeda(BigDecimal valor) {
        BigDecimal seguro = (valor == null) ? BigDecimal.ZERO : valor;
        return NumberFormat.getCurrencyInstance(BRASIL).format(seguro);
    }

    /** "2026-11-02" → "02/11/2026" */
    public static String data(String dataIso) {
        if (dataIso == null) {
            return "";
        }
        return LocalDate.parse(dataIso).format(DATA_BR);
    }

    public static String data(LocalDate data) {
        return data.format(DATA_BR);
    }

    /** YearMonth(2026, 11) → "Novembro de 2026" */
    public static String mesAno(YearMonth mes) {
        String texto = mes.format(MES_ANO);
        return texto.substring(0, 1).toUpperCase(BRASIL) + texto.substring(1);
    }

    /** 75.0 → "75%"; 12.5 → "12,5%" */
    public static String percentual(double valor) {
        NumberFormat formato = NumberFormat.getNumberInstance(BRASIL);
        formato.setMaximumFractionDigits(1);
        return formato.format(valor) + "%";
    }

    /**
     * Converte o texto digitado pelo usuário em valor. Aceita "12,50" ou "12.50".
     * @return o valor, ou null se o texto for inválido
     */
    public static BigDecimal converterValor(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** "#4CAF50" → int de cor; usa cinza se o texto for inválido. */
    public static int cor(String hex) {
        try {
            return Color.parseColor(hex);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Color.GRAY;
        }
    }
}
