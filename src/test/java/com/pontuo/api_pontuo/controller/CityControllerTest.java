package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.service.CityService;
import com.pontuo.api_pontuo.support.WithApiSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A paginação das listagens é a mesma em todos os controllers (ver
 * {@link Pagination}); este teste a exercita pela listagem de cidades.
 */
@WebMvcTest(CityController.class)
@WithApiSecurity
@DisplayName("CityController")
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CityService service;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private static JwtRequestPostProcessor estudante() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ESTUDANTE"));
    }

    /** Página como o Spring Data a recebe: o índice conta a partir de 0, e não de 1 como o cliente. */
    private static Pageable pagina(int indice, int limit) {
        return PageRequest.of(indice, limit, Sort.by(Sort.Direction.ASC, "id"));
    }

    private static City florianopolis() {
        State santaCatarina = new State("Santa Catarina");
        santaCatarina.setId(1L);
        City cidade = new City("Florianópolis", santaCatarina);
        cidade.setId(11L);
        return cidade;
    }

    @Nested
    @DisplayName("paginação")
    class Paginacao {

        @Test
        @DisplayName("sem page e limit pede a página 1, de 20, ordenada por id")
        void deveUsarPaginaPadrao() throws Exception {
            when(service.findAll(null, pagina(0, 20))).thenReturn(Page.empty(pagina(0, 20)));

            mockMvc.perform(get("/api/cities").with(estudante()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(1))
                    .andExpect(jsonPath("$.limit").value(20));

            verify(service).findAll(null, pagina(0, 20));
        }

        @Test
        @DisplayName("page e limit vão para a consulta e voltam no envelope, com page a partir de 1")
        void devePaginar() throws Exception {
            when(service.findAll(null, pagina(2, 5)))
                    .thenReturn(new PageImpl<>(List.of(florianopolis()), pagina(2, 5), 42));

            mockMvc.perform(get("/api/cities").param("page", "3").param("limit", "5").with(estudante()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(11))
                    .andExpect(jsonPath("$.content[0].name").value("Florianópolis"))
                    .andExpect(jsonPath("$.content[0].state.id").value(1))
                    .andExpect(jsonPath("$.page").value(3))
                    .andExpect(jsonPath("$.limit").value(5))
                    .andExpect(jsonPath("$.totalElements").value(42))
                    .andExpect(jsonPath("$.totalPages").value(9))
                    .andExpect(jsonPath("$.last").value(false));
        }

        @Test
        @DisplayName("o filtro também é paginado")
        void devePaginarOFiltro() throws Exception {
            when(service.findByStateId(1L, null, pagina(1, 10))).thenReturn(Page.empty(pagina(1, 10)));

            mockMvc.perform(get("/api/cities").param("stateId", "1").param("page", "2").param("limit", "10")
                            .with(estudante()))
                    .andExpect(status().isOk());

            verify(service).findByStateId(1L, null, pagina(1, 10));
            verify(service, never()).findAll(any(), any());
        }

        @Test
        @DisplayName("limit acima do teto responde 400 sem chamar o serviço")
        void deveRecusarLimitAcimaDoTeto() throws Exception {
            mockMvc.perform(get("/api/cities").param("limit", "101").with(estudante()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.limit").value("limit deve estar entre 1 e 100"));

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("limit zero responde 400 sem chamar o serviço")
        void deveRecusarLimitZero() throws Exception {
            mockMvc.perform(get("/api/cities").param("limit", "0").with(estudante()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.limit").value("limit deve estar entre 1 e 100"));

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("page zero responde 400 sem chamar o serviço")
        void deveRecusarPageZero() throws Exception {
            mockMvc.perform(get("/api/cities").param("page", "0").with(estudante()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.page").value("page deve ser maior que zero"));

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("page negativo responde 400 sem chamar o serviço")
        void deveRecusarPageNegativo() throws Exception {
            mockMvc.perform(get("/api/cities").param("page", "-1").with(estudante()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.page").value("page deve ser maior que zero"));

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("search")
    class Busca {

        @Test
        @DisplayName("vai para o serviço junto do filtro e da página")
        void deveRepassarSearch() throws Exception {
            when(service.findByStateId(1L, "flori", pagina(0, 20))).thenReturn(Page.empty(pagina(0, 20)));

            mockMvc.perform(get("/api/cities").param("stateId", "1").param("search", "flori").with(estudante()))
                    .andExpect(status().isOk());

            verify(service).findByStateId(1L, "flori", pagina(0, 20));
        }

        @Test
        @DisplayName("acima de 100 caracteres responde 400 sem chamar o serviço")
        void deveRecusarSearchLongo() throws Exception {
            mockMvc.perform(get("/api/cities").param("search", "a".repeat(101)).with(estudante()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.search").value("search deve ter no máximo 100 caracteres"));

            verifyNoInteractions(service);
        }
    }
}
