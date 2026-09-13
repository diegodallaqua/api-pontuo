package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.Topic;
import com.pontuo.api_pontuo.repository.EntranceExamRepository;
import com.pontuo.api_pontuo.repository.QuestionRepository;
import com.pontuo.api_pontuo.repository.SubjectRepository;
import com.pontuo.api_pontuo.repository.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private QuestionRepository repository;

    @Mock
    private EntranceExamRepository entranceExamRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private SubjectRepository subjectRepository;

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
    @DisplayName("delete e consultas")
    class DeleteEConsultas {

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

        @Test
        @DisplayName("findByEntranceExamId falha quando o vestibular não existe")
        void deveFalharAoListarPorVestibularInexistente() {
            when(entranceExamRepository.existsById(VESTIBULAR_ID)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findByEntranceExamId(VESTIBULAR_ID));

            assertEquals("EntranceExam não encontrado: id=1", ex.getMessage());
            verify(repository, never()).findByEntranceExamId(any());
        }

        @Test
        @DisplayName("findByTopicId falha quando o tópico não existe")
        void deveFalharAoListarPorTopicoInexistente() {
            when(topicRepository.existsById(TOPICO_ID)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findByTopicId(TOPICO_ID));

            assertEquals("Topic não encontrado: id=2", ex.getMessage());
            verify(repository, never()).findByTopicId(any());
        }

        @Test
        @DisplayName("findBySubjectId falha quando a matéria não existe")
        void deveFalharAoListarPorMateriaInexistente() {
            when(subjectRepository.existsById(MATERIA_ID)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findBySubjectId(MATERIA_ID));

            assertEquals("Subject não encontrada: id=3", ex.getMessage());
            verify(repository, never()).findBySubjectId(any());
        }
    }
}
