package com.pontuo.api_pontuo.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Parâmetros comuns das listagens paginadas: {@code page}, a partir de 1, e
 * {@code limit}, o tamanho da página. As constantes ficam aqui para que
 * defaults, teto e mensagens sejam os mesmos em todos os controllers.
 */
final class Pagination {

    /** Teto do tamanho de página: sem ele uma única chamada arrastaria a tabela inteira. */
    static final int MAX_LIMIT = 100;

    static final String DEFAULT_PAGE = "1";
    static final String DEFAULT_LIMIT = "20";

    static final String PAGE_MESSAGE = "page deve ser maior que zero";
    static final String LIMIT_MESSAGE = "limit deve estar entre 1 e " + MAX_LIMIT;

    private Pagination() {
    }

    /**
     * Converte a página pedida, que começa em 1, para a do Spring Data, que
     * começa em 0. A volta fica em {@code PageResponseDTO.of}.
     *
     * <p>Ordem fixa por id: sem ordenação definida o mesmo registro poderia
     * aparecer em duas páginas, ou em nenhuma.
     */
    static PageRequest of(int page, int limit) {
        return PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.ASC, "id"));
    }
}
