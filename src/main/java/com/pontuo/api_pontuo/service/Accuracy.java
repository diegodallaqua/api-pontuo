package com.pontuo.api_pontuo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Aproveitamento em porcentagem, com duas casas: acertos / total * 100. */
final class Accuracy {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private Accuracy() {
    }

    /** Nulo quando não há total, para não inventar um 0% sem questões. */
    static BigDecimal of(long correct, long total) {
        if (total <= 0) {
            return null;
        }
        return BigDecimal.valueOf(correct)
                .multiply(CEM)
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }
}
