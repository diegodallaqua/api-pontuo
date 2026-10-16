package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.Topic;
import com.pontuo.api_pontuo.support.RequiresDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(DatabaseFixtures.class)
@RequiresDatabase
@DisplayName("QuestionRepository.search (MariaDB)")
class QuestionRepositorySearchTest {

    private static final Pageable PAGINA = PageRequest.of(0, 50, Sort.by(Sort.Direction.ASC, "id"));

    @Autowired
    private QuestionRepository repository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private DatabaseFixtures fixtures;

    private Question porMateria;
    private Question porTopico;
    private Topic topico;
    private Subject materia;

    @BeforeEach
    void criarCenario() {
        // Uma questão classificada pela matéria e outra pelo tópico da mesma
        // matéria: é o par que mostra se o filtro cobre os dois caminhos.
        porMateria = fixtures.questao("Questão classificada pela matéria");
        materia = porMateria.getSubject();
        topico = topicRepository.save(new Topic("Tópico de teste", materia));
        porTopico = repository.save(new Question("Questão classificada pelo tópico", null, (short) 2,
                porMateria.getEntranceExam(), topico, null));
    }

    private List<Long> ids(Page<Question> pagina) {
        return pagina.getContent().stream().map(Question::getId).toList();
    }

    @Test
    @DisplayName("filtro por matéria pega a questão da matéria e a do tópico dela")
    void deveFiltrarPorMateriaNosDoisCaminhos() {
        List<Long> encontradas = ids(repository.search(null, null, materia.getId(), null, null, PAGINA));

        assertAll(
                () -> assertTrue(encontradas.contains(porMateria.getId())),
                () -> assertTrue(encontradas.contains(porTopico.getId())));
    }

    @Test
    @DisplayName("filtro por área também pega os dois caminhos")
    void deveFiltrarPorAreaNosDoisCaminhos() {
        Long areaId = materia.getKnowledgeArea().getId();

        List<Long> encontradas = ids(repository.search(null, areaId, null, null, null, PAGINA));

        assertAll(
                () -> assertTrue(encontradas.contains(porMateria.getId())),
                () -> assertTrue(encontradas.contains(porTopico.getId())));
    }

    @Test
    @DisplayName("os filtros se somam: vestibular, matéria e tópico juntos")
    void deveCombinarOsFiltros() {
        List<Long> encontradas = ids(repository.search(
                porTopico.getEntranceExam().getId(), null, materia.getId(), topico.getId(), null,
                PAGINA));

        assertAll(
                () -> assertTrue(encontradas.contains(porTopico.getId())),
                () -> assertFalse(encontradas.contains(porMateria.getId()),
                        "a questão sem tópico não pode passar pelo filtro de tópico"));
    }

    @Test
    @DisplayName("filtro por dificuldade descarta as questões de outro nível")
    void deveFiltrarPorDificuldade() {
        short doTopico = porTopico.getDifficulty();
        short outroNivel = (short) (doTopico == 3 ? 1 : doTopico + 1);

        List<Long> mesmoNivel = ids(
                repository.search(null, null, materia.getId(), null, doTopico, PAGINA));
        List<Long> nivelDiferente = ids(
                repository.search(null, null, materia.getId(), null, outroNivel, PAGINA));

        assertAll(
                () -> assertTrue(mesmoNivel.contains(porTopico.getId())),
                () -> assertFalse(nivelDiferente.contains(porTopico.getId())));
    }

    @Test
    @DisplayName("searchIds devolve os mesmos ids da busca")
    void deveListarOsMesmosIds() {
        List<Long> daBusca = ids(repository.search(null, null, materia.getId(), null, null, PAGINA));
        List<Long> doSorteio = repository.searchIds(null, null, materia.getId(), null, null);

        assertAll(
                () -> assertEquals(daBusca.size(), doSorteio.size()),
                () -> assertTrue(doSorteio.containsAll(daBusca)));
    }

    @Test
    @DisplayName("a página respeita o size e ainda conta o total")
    void devePaginar() {
        Page<Question> pagina = repository.search(null, null, materia.getId(), null, null,
                PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id")));

        assertAll(
                () -> assertEquals(1, pagina.getContent().size()),
                () -> assertTrue(pagina.getTotalElements() >= 2),
                () -> assertFalse(pagina.isLast()));
    }
}
