package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.dto.QuestionFilterDTO;
import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.QuestionImage;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.Topic;
import com.pontuo.api_pontuo.repository.AnswerOptionRepository;
import com.pontuo.api_pontuo.repository.EntranceExamRepository;
import com.pontuo.api_pontuo.repository.KnowledgeAreaRepository;
import com.pontuo.api_pontuo.repository.QuestionImageRepository;
import com.pontuo.api_pontuo.repository.QuestionRepository;
import com.pontuo.api_pontuo.repository.SubjectRepository;
import com.pontuo.api_pontuo.repository.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuestionService")
class QuestionServiceTest {

    private static final Long VESTIBULAR_ID = 1L;
    private static final Long TOPICO_ID = 2L;
    private static final Long MATERIA_ID = 3L;
    private static final Long AREA_ID = 4L;

    @Mock
    private QuestionRepository repository;

    @Mock
    private EntranceExamRepository entranceExamRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private KnowledgeAreaRepository knowledgeAreaRepository;

    @Mock
    private AnswerOptionRepository answerOptionRepository;

    @Mock
    private QuestionImageRepository questionImageRepository;

    @InjectMocks
    private QuestionService service;

    private EntranceExam vestibular;
    private Topic topico;
    private Subject materia;
    private Question questao;

    @BeforeEach
    void setUp() {
        vestibular = new EntranceExam("ENEM", (short) 2026, "1º Dia");
        vestibular.setId(VESTIBULAR_ID);
        topico = new Topic("Genética");
        topico.setId(TOPICO_ID);
        materia = new Subject("Biologia");
        materia.setId(MATERIA_ID);
        questao = new Question("Enunciado", "Explicação", (short) 2);
    }

    private static QuestionFilterDTO semFiltro() {
        return new QuestionFilterDTO(null, null, null, null, null);
    }

    private static Question comId(Long id) {
        Question questao = new Question("Enunciado", null, (short) 2);
        questao.setId(id);
        return questao;
    }

    private void salvarDevolvendoOArgumento() {
        when(repository.save(any(Question.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("classifica pelo tópico e deixa a matéria vazia")
        void deveClassificarPorTopico() {
            when(entranceExamRepository.findById(VESTIBULAR_ID)).thenReturn(Optional.of(vestibular));
            when(topicRepository.findById(TOPICO_ID)).thenReturn(Optional.of(topico));
            salvarDevolvendoOArgumento();

            Question criada = service.create(questao, VESTIBULAR_ID, TOPICO_ID, null);

            assertAll(
                    () -> assertSame(vestibular, criada.getEntranceExam()),
                    () -> assertSame(topico, criada.getTopic()),
                    () -> assertNull(criada.getSubject()));
            verifyNoInteractions(subjectRepository);
        }

        @Test
        @DisplayName("classifica pela matéria e deixa o tópico vazio")
        void deveClassificarPorMateria() {
            when(entranceExamRepository.findById(VESTIBULAR_ID)).thenReturn(Optional.of(vestibular));
            when(subjectRepository.findById(MATERIA_ID)).thenReturn(Optional.of(materia));
            salvarDevolvendoOArgumento();

            Question criada = service.create(questao, VESTIBULAR_ID, null, MATERIA_ID);

            assertAll(
                    () -> assertSame(materia, criada.getSubject()),
                    () -> assertNull(criada.getTopic()));
            verifyNoInteractions(topicRepository);
        }

        @Test
        @DisplayName("exige tópico ou matéria antes de consultar qualquer repositório")
        void deveExigirTopicoOuMateria() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(questao, VESTIBULAR_ID, null, null));

            assertEquals("informe topicId ou subjectId", ex.getMessage());
            verifyNoInteractions(repository, entranceExamRepository, topicRepository, subjectRepository);
        }

        @Test
        @DisplayName("rejeita tópico e matéria juntos, que poderiam se contradizer")
        void deveRejeitarTopicoEMateriaJuntos() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(questao, VESTIBULAR_ID, TOPICO_ID, MATERIA_ID));

            assertEquals("informe topicId ou subjectId, não os dois", ex.getMessage());
            verifyNoInteractions(repository, entranceExamRepository, topicRepository, subjectRepository);
        }

        @Test
        @DisplayName("falha quando o vestibular não existe")
        void deveFalharQuandoVestibularNaoExiste() {
            when(entranceExamRepository.findById(VESTIBULAR_ID)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(questao, VESTIBULAR_ID, TOPICO_ID, null));

            assertEquals("EntranceExam não encontrado: id=1", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando o tópico não existe")
        void deveFalharQuandoTopicoNaoExiste() {
            when(entranceExamRepository.findById(VESTIBULAR_ID)).thenReturn(Optional.of(vestibular));
            when(topicRepository.findById(TOPICO_ID)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(questao, VESTIBULAR_ID, TOPICO_ID, null));

            assertEquals("Topic não encontrado: id=2", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a matéria não existe")
        void deveFalharQuandoMateriaNaoExiste() {
            when(entranceExamRepository.findById(VESTIBULAR_ID)).thenReturn(Optional.of(vestibular));
            when(subjectRepository.findById(MATERIA_ID)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(questao, VESTIBULAR_ID, null, MATERIA_ID));

            assertEquals("Subject não encontrada: id=3", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        private Question existente;

        @BeforeEach
        void setUpExistente() {
            existente = new Question("Enunciado antigo", null, (short) 1, vestibular, topico, null);
            existente.setId(50L);
        }

        @Test
        @DisplayName("atualiza os campos e troca o tópico pela matéria")
        void deveAtualizarCamposETrocarClassificacao() {
            when(repository.findById(50L)).thenReturn(Optional.of(existente));
            when(entranceExamRepository.findById(VESTIBULAR_ID)).thenReturn(Optional.of(vestibular));
            when(subjectRepository.findById(MATERIA_ID)).thenReturn(Optional.of(materia));
            salvarDevolvendoOArgumento();

            Question resultado = service.update(50L, questao, VESTIBULAR_ID, null, MATERIA_ID);

            assertAll(
                    () -> assertEquals("Enunciado", resultado.getStatement()),
                    () -> assertEquals("Explicação", resultado.getExplanation()),
                    () -> assertEquals((short) 2, resultado.getDifficulty()),
                    () -> assertSame(materia, resultado.getSubject()),
                    () -> assertNull(resultado.getTopic(), "o tópico antigo não pode sobrar junto com a matéria"));
        }

        @Test
        @DisplayName("aplica a mesma regra de classificação do create")
        void deveAplicarRegraDeClassificacao() {
            when(repository.findById(50L)).thenReturn(Optional.of(existente));

            assertThrows(IllegalArgumentException.class,
                    () -> service.update(50L, questao, VESTIBULAR_ID, TOPICO_ID, MATERIA_ID));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a questão não existe")
        void deveFalharQuandoQuestaoNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, questao, VESTIBULAR_ID, TOPICO_ID, null));

            assertEquals("Question não encontrada: id=404", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("delete remove a questão existente")
        void deveRemoverExistente() {
            when(repository.existsById(50L)).thenReturn(true);

            service.delete(50L);

            verify(repository).deleteById(50L);
        }

        @Test
        @DisplayName("delete falha quando a questão não existe")
        void deveFalharAoRemoverInexistente() {
            when(repository.existsById(404L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("Question não encontrada: id=404", ex.getMessage());
            verify(repository, never()).deleteById(any());
        }

    }

    @Nested
    @DisplayName("search")
    class Search {

        @Test
        @DisplayName("repassa os quatro filtros juntos, nenhum sobrepõe o outro")
        void deveCombinarOsFiltros() {
            Pageable pagina = PageRequest.of(0, 20);
            Page<Question> esperada = new PageImpl<>(List.of(questao));
            when(entranceExamRepository.existsById(VESTIBULAR_ID)).thenReturn(true);
            when(knowledgeAreaRepository.existsById(AREA_ID)).thenReturn(true);
            when(subjectRepository.existsById(MATERIA_ID)).thenReturn(true);
            when(topicRepository.existsById(TOPICO_ID)).thenReturn(true);
            when(repository.search(VESTIBULAR_ID, AREA_ID, MATERIA_ID, TOPICO_ID, null, pagina))
                    .thenReturn(esperada);

            Page<Question> resultado = service.search(
                    new QuestionFilterDTO(VESTIBULAR_ID, AREA_ID, MATERIA_ID, TOPICO_ID, null),
                    pagina);

            assertSame(esperada, resultado);
        }

        @Test
        @DisplayName("sem filtro não consulta nenhum repositório de validação")
        void deveBuscarTudoSemValidarNada() {
            Pageable pagina = PageRequest.of(0, 20);
            when(repository.search(null, null, null, null, null, pagina))
                    .thenReturn(new PageImpl<>(List.of()));

            service.search(semFiltro(), pagina);

            verifyNoInteractions(entranceExamRepository, topicRepository,
                    subjectRepository, knowledgeAreaRepository);
        }

        @Test
        @DisplayName("área inexistente é 404, não lista vazia")
        void deveFalharComAreaInexistente() {
            when(knowledgeAreaRepository.existsById(AREA_ID)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.search(new QuestionFilterDTO(null, AREA_ID, null, null, null),
                            PageRequest.of(0, 20)));

            assertEquals("KnowledgeArea não encontrada: id=4", ex.getMessage());
            verify(repository, never()).search(any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("vestibular inexistente é 404, mesmo com os outros filtros válidos")
        void deveFalharComVestibularInexistente() {
            when(entranceExamRepository.existsById(VESTIBULAR_ID)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.search(new QuestionFilterDTO(VESTIBULAR_ID, null, null, TOPICO_ID, null),
                            PageRequest.of(0, 20)));

            assertEquals("EntranceExam não encontrado: id=1", ex.getMessage());
            verify(repository, never()).search(any(), any(), any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("draw")
    class Draw {

        @Test
        @DisplayName("sorteia a quantidade pedida entre as candidatas, sem repetir")
        void deveSortearAQuantidadePedida() {
            List<Long> candidatas = List.of(1L, 2L, 3L, 4L, 5L);
            when(repository.searchIds(null, null, null, null, null)).thenReturn(candidatas);
            when(repository.findByIdIn(anyCollection())).thenAnswer(invocation ->
                    idsDoArgumento(invocation.getArgument(0)).stream()
                            .map(QuestionServiceTest::comId)
                            .toList());

            List<Long> sorteadas = service.draw(semFiltro(), 3).stream()
                    .map(sorteada -> sorteada.question().getId())
                    .toList();

            assertAll(
                    () -> assertEquals(3, sorteadas.size()),
                    () -> assertEquals(3, sorteadas.stream().distinct().count()),
                    () -> assertTrue(candidatas.containsAll(sorteadas)));
        }

        @Test
        @DisplayName("mantém a ordem do sorteio, e não a ordem do banco")
        void deveManterAOrdemDoSorteio() {
            when(repository.searchIds(null, null, null, null, null)).thenReturn(List.of(1L, 2L, 3L));
            // O banco devolve na ordem dele; aqui, invertida de propósito.
            when(repository.findByIdIn(anyCollection())).thenAnswer(invocation -> {
                List<Long> ids = new ArrayList<>(idsDoArgumento(invocation.getArgument(0)));
                Collections.reverse(ids);
                return ids.stream().map(QuestionServiceTest::comId).toList();
            });

            List<Long> sorteadas = service.draw(semFiltro(), 3).stream()
                    .map(sorteada -> sorteada.question().getId())
                    .toList();

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Collection<Long>> pedidos = ArgumentCaptor.forClass(Collection.class);
            verify(repository).findByIdIn(pedidos.capture());
            assertEquals(List.copyOf(pedidos.getValue()), sorteadas);
        }

        @Test
        @DisplayName("devolve o que existe quando há menos questões que o pedido")
        void deveDevolverOQueExiste() {
            when(repository.searchIds(null, null, null, null, null)).thenReturn(List.of(7L, 8L));
            when(repository.findByIdIn(anyCollection())).thenAnswer(invocation ->
                    idsDoArgumento(invocation.getArgument(0)).stream()
                            .map(QuestionServiceTest::comId)
                            .toList());

            assertEquals(2, service.draw(semFiltro(), 10).size());
        }

        @Test
        @DisplayName("sem candidata não vai ao banco buscar questão nenhuma")
        void deveDevolverVazioSemCandidatas() {
            when(repository.searchIds(null, null, null, null, null)).thenReturn(List.of());

            assertEquals(List.of(), service.draw(semFiltro(), 10));
            verify(repository, never()).findByIdIn(any());
        }

        @Test
        @DisplayName("cada questão recebe as alternativas e imagens que são dela")
        void deveAgruparAlternativasEImagensPorQuestao() {
            when(repository.searchIds(null, null, null, null, null)).thenReturn(List.of(1L, 2L));
            when(repository.findByIdIn(anyCollection())).thenAnswer(invocation ->
                    idsDoArgumento(invocation.getArgument(0)).stream()
                            .map(QuestionServiceTest::comId)
                            .toList());
            when(answerOptionRepository.findByQuestionIdIn(anyCollection()))
                    .thenReturn(List.of(alternativa("A", comId(1L)), alternativa("B", comId(2L))));
            when(questionImageRepository.findByQuestionIdIn(anyCollection()))
                    .thenReturn(List.of(imagem(comId(2L))));

            Map<Long, DrawnQuestion> porQuestao = service.draw(semFiltro(), 2).stream()
                    .collect(Collectors.toMap(sorteada -> sorteada.question().getId(), sorteada -> sorteada));

            assertAll(
                    () -> assertEquals(List.of("A"), letras(porQuestao.get(1L))),
                    () -> assertEquals(List.of("B"), letras(porQuestao.get(2L))),
                    () -> assertEquals(0, porQuestao.get(1L).images().size()),
                    () -> assertEquals(1, porQuestao.get(2L).images().size()));
        }

        @Test
        @DisplayName("sem candidata não busca alternativa nem imagem")
        void naoDeveBuscarConteudoSemCandidatas() {
            when(repository.searchIds(null, null, null, null, null)).thenReturn(List.of());

            service.draw(semFiltro(), 10);

            verifyNoInteractions(answerOptionRepository, questionImageRepository);
        }

        private List<String> letras(DrawnQuestion sorteada) {
            return sorteada.answerOptions().stream().map(AnswerOption::getLetter).toList();
        }

        private AnswerOption alternativa(String letra, Question questao) {
            return new AnswerOption(letra, "Texto " + letra, false, questao);
        }

        private QuestionImage imagem(Question questao) {
            return new QuestionImage("https://exemplo/" + questao.getId() + ".png", null, questao);
        }

        @Test
        @DisplayName("valida os filtros antes de sortear")
        void deveValidarFiltros() {
            when(topicRepository.existsById(TOPICO_ID)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.draw(new QuestionFilterDTO(null, null, null, TOPICO_ID, null), 5));

            assertEquals("Topic não encontrado: id=2", ex.getMessage());
            verify(repository, never()).searchIds(any(), any(), any(), any(), any());
        }

        @SuppressWarnings("unchecked")
        private List<Long> idsDoArgumento(Object argument) {
            return List.copyOf((Collection<Long>) argument);
        }
    }
}
