package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.Institution;
import com.pontuo.api_pontuo.support.RequiresDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiresDatabase
@DisplayName("EntranceExamRepository (MariaDB)")
class EntranceExamRepositoryTest {

    private static final long INEP_ID = 1L;

    @Autowired
    private EntranceExamRepository repository;

    @Autowired
    private InstitutionRepository institutionRepository;

    private Institution inep;

    @BeforeEach
    void carregarInstituicao() {
        inep = institutionRepository.findById(INEP_ID).orElseThrow();
    }

    private EntranceExam vestibularDeTeste(String etapa) {
        return new EntranceExam("Vestibular JUnit", (short) 2027, etapa, inep);
    }

    @Test
    @DisplayName("migrations cadastram as duas etapas do ENEM 2026")
    void deveTerAsDuasEtapasDoEnem() {
        Set<String> etapas = repository.findByInstitutionId(INEP_ID).stream()
                .filter(exame -> exame.getName().equals("ENEM") && exame.getYear() == 2026)
                .map(EntranceExam::getStage)
                .collect(Collectors.toSet());

        assertTrue(etapas.containsAll(Set.of("1º Dia", "2º Dia")), "etapas encontradas: " + etapas);
    }

    @Test
    @DisplayName("aceita o mesmo vestibular em etapas diferentes")
    void deveAceitarEtapasDiferentes() {
        assertDoesNotThrow(() -> {
            repository.saveAndFlush(vestibularDeTeste("1ª Fase"));
            repository.saveAndFlush(vestibularDeTeste("2ª Fase"));
        });
    }

    @Test
    @DisplayName("recusa a mesma etapa repetida")
    void deveRecusarEtapaRepetida() {
        repository.saveAndFlush(vestibularDeTeste("1ª Fase"));

        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(vestibularDeTeste("1ª Fase")));
    }

    @Test
    @DisplayName("recusa vestibular sem etapa")
    void deveRecusarEtapaNula() {
        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(vestibularDeTeste(null)));
    }
}
