package com.meubolso.app.util;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Utilitários de tela.
 * Nas versões novas do Android o app ocupa a tela toda (inclusive atrás da barra de status),
 * então aplicamos um espaçamento para o conteúdo não ficar escondido.
 */
public final class Telas {

    private Telas() {
    }

    /** Chame ANTES de setContentView. */
    public static void ativarTelaCheia(ComponentActivity activity) {
        EdgeToEdge.enable(activity,
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
    }

    /** Chame DEPOIS de setContentView, passando a view raiz do layout. */
    public static void aplicarEspacamento(View raiz) {
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (view, insets) -> {
            Insets barras = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            view.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /** Lê o texto de um campo sem espaços nas pontas (nunca retorna null). */
    public static String texto(TextView campo) {
        CharSequence conteudo = campo.getText();
        return conteudo == null ? "" : conteudo.toString().trim();
    }
}
