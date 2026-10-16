package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.QuestionFilterDTO;
import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.QuestionImage;
import com.pontuo.api_pontuo.service.DrawnQuestion;
import com.pontuo.api_pontuo.service.QuestionService;
import com.pontuo.api_pontuo.support.WithApiSecurity;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
@WithApiSecurity
@DisplayName("QuestionController")
class QuestionControllerTest {

    private static final String QUESTAO_JSON = """
            {"statement": "Enunciado", "difficulty": 2, "entranceExamId": 1, "topicId": 2}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuestionService service;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private static JwtRequestPostProcessor administrador() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
    }

    private static Question questao(Long id) {
        Question questao = new Question("Enunciado", "Explicação", (short) 2);
        questao.setId(id);
        return questao;
    }

    private static DrawnQuestion sorteada(Long id) {
        Question questao = questao(id);
        AnswerOption alternativa = new AnswerOption("A", "Alternativa A", true, questao);
        alternativa.setId(id * 10);
        QuestionImage imagem = new QuestionImage("https://exemplo/" + id + ".png", "Figura", questao);
        imagem.setId(id * 100);
        return new DrawnQuestion(questao, List.of(alternativa), List.of(imagem));
    }

    private void paginaVazia() {
        when(service.search(any(), any())).thenReturn(new PageImpl<>(List.of()));
    }

    @Nested
    @DisplayName("filtros da listagem")
    class Filtros {

        @Test
        @DisplayName("os quatro filtros são aplicados juntos, nenhum é ignorado")
        void deveCombinarOsFiltros() throws Exception {
            paginaVazia();

            mockMvc.perform(get("/api/questions")
                            .param("entranceExamId", "1")
                            .param("knowledgeAreaId", "2")
                            .param("subjectId", "3")
                            .param("topicId", "4")
                            .param("difficulty", "3")
                            .with(administrador()))
                    .andExpect(status().isOk());

            ArgumentCaptor<QuestionFilterDTO> filtro = ArgumentCaptor.forClass(QuestionFilterDTO.class);
            verify(service).search(filtro.capture(), any());
            assertEquals(new QuestionFilterDTO(1L, 2L, 3L, 4L, (short) 3), filtro.getValue());
        }

        @Test
        @DisplayName("sem filtro busca tudo, com filtro nulo em todos os campos")
        void deveBuscarTudoSemFiltro() throws Exception {
            paginaVazia();

            mockMvc.perform(get("/api/questions").with(administrador()))
                    .andExpect(status().isOk());

            verify(service).search(new QuestionFilterDTO(null, null, null, null, null),
                    PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id")));
        }

        @Test
        @DisplayName("filtro por área inexistente responde 404 com mensagem")
        void deveResponder404ParaAreaInexistente() throws Exception {
            when(service.search(any(), any()))
                    .thenThrow(new EntityNotFoundException("KnowledgeArea não encontrada: id=99"));

            mockMvc.perform(get("/api/questions").param("knowledgeAreaId", "99").with(administrador()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("KnowledgeArea não encontrada: id=99"));
        }
    }

    @Nested
    @DisplayName("paginação")
    class Paginacao {

        @Test
        @DisplayName("page e size vão para a consulta e voltam no envelope")
        void devePaginar() throws Exception {
            Pageable pagina = PageRequest.of(2, 5, Sort.by(Sort.Direction.ASC, "id"));
            when(service.search(any(), eq(pagina)))
                    .thenReturn(new PageImpl<>(List.of(questao(11L)), pagina, 42));

            mockMvc.perform(get("/api/questions").param("page", "2").param("size", "5")
                            .with(administrador()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(11))
                    .andExpect(jsonPath("$.page").value(2))
                    .andExpect(jsonPath("$.size").value(5))
                    .andExpect(jsonPath("$.totalElements").value(42))
                    .andExpect(jsonPath("$.totalPages").value(9))
                    .andExpect(jsonPath("$.last").value(false));
        }

        @Test
        @DisplayName("size acima do teto responde 400 sem chamar o serviço")
        void deveRecusarSizeAcimaDoTeto() throws Exception {
            mockMvc.perform(get("/api/questions").param("size", "101").with(administrador()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.size").value("size deve estar entre 1 e 100"));

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("page negativo responde 400 sem chamar o serviço")
        void deveRecusarPageNegativo() throws Exception {
            mockMvc.perform(get("/api/questions").param("page", "-1").with(administrador()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.page").value("page não pode ser negativo"));

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("sorteio")
    class Sorteio {

        @Test
        @DisplayName("sorteia a quantidade pedida com os mesmos filtros da listagem")
        void deveSortearComFiltros() throws Exception {
            when(service.draw(any(), anyInt())).thenReturn(List.of(sorteada(3L), sorteada(9L)));

            mockMvc.perform(get("/api/questions/draw")
                            .param("knowledgeAreaId", "2")
                            .param("size", "2")
                            .with(administrador()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(3))
                    .andExpect(jsonPath("$[1].id").value(9));

            verify(service).draw(new QuestionFilterDTO(null, 2L, null, null, null), 2);
        }

        @Test
        @DisplayName("sem size sorteia 10 questões")
        void deveUsarSizePadrao() throws Exception {
            when(service.draw(any(), anyInt())).thenReturn(List.of());

            mockMvc.perform(get("/api/questions/draw").with(administrador()))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));

            verify(service).draw(new QuestionFilterDTO(null, null, null, null, null), 10);
        }

        @Test
        @DisplayName("alternativas e imagens vêm embutidas na questão sorteada")
        void deveEmbutirAlternativasEImagens() throws Exception {
            when(service.draw(any(), anyInt())).thenReturn(List.of(sorteada(3L)));

            mockMvc.perform(get("/api/questions/draw").with(administrador()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].answerOptions[0].id").value(30))
                    .andExpect(jsonPath("$[0].answerOptions[0].letter").value("A"))
                    .andExpect(jsonPath("$[0].answerOptions[0].text").value("Alternativa A"))
                    .andExpect(jsonPath("$[0].images[0].id").value(300))
                    .andExpect(jsonPath("$[0].images[0].url").value("https://exemplo/3.png"));
        }

        @Test
        @DisplayName("o gabarito não viaja: sem rightAnswer e sem explanation")
        void naoDeveVazarGabarito() throws Exception {
            when(service.draw(any(), anyInt())).thenReturn(List.of(sorteada(3L)));

            mockMvc.perform(get("/api/questions/draw").with(administrador()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].explanation").doesNotExist())
                    .andExpect(jsonPath("$[0].answerOptions[0].rightAnswer").doesNotExist());
        }

        @Test
        @DisplayName("difficulty entra no filtro do sorteio")
        void deveSortearPorDificuldade() throws Exception {
            when(service.draw(any(), anyInt())).thenReturn(List.of());

            mockMvc.perform(get("/api/questions/draw").param("difficulty", "2")
                            .with(administrador()))
                    .andExpect(status().isOk());

            verify(service).draw(new QuestionFilterDTO(null, null, null, null, (short) 2), 10);
        }

        @Test
        @DisplayName("difficulty fora de 1..3 responde 400 sem chamar o serviço")
        void deveRecusarDificuldadeInvalida() throws Exception {
            mockMvc.perform(get("/api/questions/draw").param("difficulty", "4")
                            .with(administrador()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.difficulty").value("difficulty deve estar entre 1 e 3"));

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("size zero responde 400 sem chamar o serviço")
        void deveRecusarSizeZero() throws Exception {
            mockMvc.perform(get("/api/questions/draw").param("size", "0").with(administrador()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.size").value("size deve estar entre 1 e 100"));

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("respostas de erro")
    class Erros {

        @Test
        @DisplayName("GET de id inexistente responde 404 sem corpo")
        void deveResponder404SemCorpo() throws Exception {
            when(service.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/questions/99").with(administrador()))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("corpo inválido responde 400 por campo, sem chamar o serviço")
        void deveResponder400ParaCorpoInvalido() throws Exception {
            mockMvc.perform(post("/api/questions").with(administrador())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"statement\": \"\", \"difficulty\": 4}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.statement").value("statement é obrigatório"))
                    .andExpect(jsonPath("$.difficulty").value("difficulty deve estar entre 1 e 3"))
                    .andExpect(jsonPath("$.entranceExamId").value("entranceExamId é obrigatório"));

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("regra de negócio violada responde 409")
        void deveResponder409ParaRegraViolada() throws Exception {
            when(service.create(any(), eq(1L), eq(2L), isNull()))
                    .thenThrow(new IllegalArgumentException("informe topicId ou subjectId, não os dois"));

            mockMvc.perform(post("/api/questions").with(administrador())
                            .contentType(MediaType.APPLICATION_JSON).content(QUESTAO_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("informe topicId ou subjectId, não os dois"));
        }

        @Test
        @DisplayName("PUT de id inexistente responde 404 com mensagem")
        void deveResponder404NoUpdate() throws Exception {
            when(service.update(eq(99L), any(), anyLong(), anyLong(), isNull()))
                    .thenThrow(new EntityNotFoundException("Question não encontrada: id=99"));

            mockMvc.perform(put("/api/questions/99").with(administrador())
                            .contentType(MediaType.APPLICATION_JSON).content(QUESTAO_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Question não encontrada: id=99"));
        }
    }

    @Nested
    @DisplayName("contrato do envelope")
    class Envelope {

        @Test
        @DisplayName("página vazia ainda traz os campos de paginação")
        void deveDevolverEnvelopeVazio() throws Exception {
            Page<Question> vazia = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
            when(service.search(any(), any())).thenReturn(vazia);

            mockMvc.perform(get("/api/questions").with(administrador()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.totalElements").value(0))
                    .andExpect(jsonPath("$.last").value(true));
        }
    }
}
