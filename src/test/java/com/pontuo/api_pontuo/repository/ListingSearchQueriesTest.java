package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.support.RequiresDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * As consultas de listagem são JPQL escrito à mão, e a contagem da página é
 * derivada dele pelo Spring Data. Este teste roda cada uma no MariaDB com
 * search e página pequena, para que tanto a consulta quanto a contagem passem.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiresDatabase
@DisplayName("Consultas search das listagens (MariaDB)")
class ListingSearchQueriesTest {

    private static final Pageable PAGINA = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id"));
    private static final String TEXTO = LikePattern.contains("a");

    @Autowired private StateRepository states;
    @Autowired private CityRepository cities;
    @Autowired private AddressRepository addresses;
    @Autowired private InstitutionRepository institutions;
    @Autowired private EntranceExamRepository entranceExams;
    @Autowired private KnowledgeAreaRepository knowledgeAreas;
    @Autowired private SubjectRepository subjects;
    @Autowired private TopicRepository topics;
    @Autowired private AnswerOptionRepository answerOptions;
    @Autowired private QuestionImageRepository questionImages;
    @Autowired private MockExamRepository mockExams;
    @Autowired private MockExamHasQuestionRepository mockExamQuestions;
    @Autowired private UserRepository users;
    @Autowired private UserRoleRepository userRoles;

    @Test
    @DisplayName("cada consulta roda com filtros e search, e conta o total")
    void deveExecutarTodas() {
        Map<String, Supplier<Page<?>>> consultas = Map.ofEntries(
                Map.entry("states", () -> states.search(TEXTO, PAGINA)),
                Map.entry("cities", () -> cities.search(1L, TEXTO, PAGINA)),
                Map.entry("addresses", () -> addresses.search(1L, TEXTO, PAGINA)),
                Map.entry("institutions", () -> institutions.search("INEP", TEXTO, PAGINA)),
                Map.entry("entrance-exams", () -> entranceExams.search(1L, (short) 2025, TEXTO, PAGINA)),
                Map.entry("knowledge-areas", () -> knowledgeAreas.search(TEXTO, PAGINA)),
                Map.entry("subjects", () -> subjects.search(1L, TEXTO, PAGINA)),
                Map.entry("topics", () -> topics.search(1L, TEXTO, PAGINA)),
                Map.entry("answer-options", () -> answerOptions.search(1L, TEXTO, PAGINA)),
                Map.entry("question-images", () -> questionImages.search(1L, TEXTO, PAGINA)),
                Map.entry("mock-exams", () -> mockExams.search(1L, TEXTO, PAGINA)),
                Map.entry("mock-exam-questions", () -> mockExamQuestions.search(1L, 1L, 1L, TEXTO, PAGINA)),
                Map.entry("users", () -> users.search(1L, TEXTO, PAGINA)),
                Map.entry("user-roles", () -> userRoles.search(TEXTO, PAGINA)));

        assertAll(consultas.entrySet().stream().map(consulta -> (Executable) () -> {
            Page<?> pagina = consulta.getValue().get();
            assertNotNull(pagina, consulta.getKey());
            assertTrue(pagina.getContent().size() <= 1, consulta.getKey() + " ignorou o limit");
            assertTrue(pagina.getTotalElements() >= pagina.getContent().size(), consulta.getKey() + " contou errado");
        }));
    }
}
