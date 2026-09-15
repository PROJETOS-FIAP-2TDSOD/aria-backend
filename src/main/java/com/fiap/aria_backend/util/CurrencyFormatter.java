package com.fiap.aria_backend.util;

import java.text.DecimalFormat;

public final class CurrencyFormatter {

    private CurrencyFormatter() {
    }

    public static String toCompactReais(double valueReais) {
        if (valueReais == 0) return "R$ 0,00";
        String sign = valueReais < 0 ? "-" : "";
        double abs = Math.abs(valueReais);
        if (abs >= 1_000_000) {
            return sign + "R$ " + new DecimalFormat("#,##0.#").format(abs / 1_000_000.0) + "M";
        } else if (abs >= 1_000) {
            return sign + "R$ " + new DecimalFormat("#,##0.#").format(abs / 1_000.0) + "k";
        }
        return sign + "R$ " + new DecimalFormat("#,##0.00").format(abs);
    }
}