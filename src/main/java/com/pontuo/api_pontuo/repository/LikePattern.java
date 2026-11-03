package com.pontuo.api_pontuo.repository;

import java.util.Locale;

/**
 * Monta o padrão do parâmetro {@code :search} das consultas de listagem, que
 * comparam {@code LOWER(campo) LIKE :search ESCAPE '!'}.
 *
 * <p>{@code %} e {@code _} digitados pelo usuário são escapados: sem isso
 * "100%" casaria com qualquer texto que começasse por "100". O escape é
 * {@code !}, e não a barra, porque o MariaDB trata a barra como escape também
 * dentro do literal SQL, e {@code ESCAPE '\'} viraria um literal aberto.
 */
public final class LikePattern {

    private LikePattern() {
    }

    /**
     * Padrão "contém", em minúsculas, ou {@code null} quando não há o que
     * buscar: nas consultas, {@code :search IS NULL} desliga o filtro.
     */
    public static String contains(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }
        String escaped = search.strip().toLowerCase(Locale.ROOT)
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return "%" + escaped + "%";
    }
}
