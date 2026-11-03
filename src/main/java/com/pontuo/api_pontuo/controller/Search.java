package com.pontuo.api_pontuo.controller;

/**
 * Parâmetro {@code search} das listagens: trecho de texto procurado nos campos
 * de cada recurso, sem diferenciar maiúsculas. Vazio ou só espaços é ignorado.
 */
final class Search {

    /** Teto do texto buscado: um LIKE com um padrão enorme só gasta o banco. */
    static final int MAX_LENGTH = 100;

    static final String MESSAGE = "search deve ter no máximo " + MAX_LENGTH + " caracteres";

    private Search() {
    }
}
