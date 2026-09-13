package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.Institution;
import com.pontuo.api_pontuo.repository.EntranceExamRepository;
import com.pontuo.api_pontuo.repository.InstitutionRepository;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EntranceExamService")
class EntranceExamServiceTest {

    @Mock
    private EntranceExamRepository repository;

    @Mock
    private InstitutionRepository institutionRepository;

    @InjectMocks
    private EntranceExamService service;

    private Institution inep;

    @BeforeEach
    void setUp() {
        inep = new Institution("Instituto Nacional", "INEP");
        inep.setId(1L);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("associa o vestibular à instituição")
        void deveAssociarInstituicao() {
            when(institutionRepository.findById(1L)).thenReturn(Optional.of(inep));
            when(repository.save(any(EntranceExam.class))).thenAnswer(invocation -> invocation.getArgument(0));

            EntranceExam criado = service.create(new EntranceExam("ENEM", (short) 2026, "1º Dia"), 1L);

            assertSame(inep, criado.getInstitution());
        }

        @Test
        @DisplayName("falha quando a instituição não existe")
        void deveFalharQuandoInstituicaoNaoExiste() {
            when(institutionRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(new EntranceExam("ENEM", (short) 2026, "1º Dia"), 99L));

            assertEquals("Institution não encontrada: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        private EntranceExam existente;

        @BeforeEach
        void setUpExistente() {
            existente = new EntranceExam("ENEM", (short) 2025, "1º Dia", inep);
            existente.setId(1L);
        }

        @Test
        @DisplayName("atualiza nome, ano, etapa e instituição do vestibular existente")
        void deveAtualizarCampos() {
            Institution fuvest = new Institution("Fundação Universitária", "FUVEST");
            fuvest.setId(2L);
            when(repository.findById(1L)).thenReturn(Optional.of(existente));
            when(institutionRepository.findById(2L)).thenReturn(Optional.of(fuvest));
            when(repository.save(any(EntranceExam.class))).thenAnswer(invocation -> invocation.getArgument(0));

            EntranceExam resultado = service.update(1L,
                    new EntranceExam("Vestibular FUVEST (USP)", (short) 2026, "2ª Fase"), 2L);

            assertAll(
                    () -> assertEquals(1L, resultado.getId()),
                    () -> assertEquals("Vestibular FUVEST (USP)", resultado.getName()),
                    () -> assertEquals(2026, resultado.getYear()),
                    () -> assertEquals("2ª Fase", resultado.getStage()),
                    () -> assertSame(fuvest, resultado.getInstitution()));
        }

        @Test
        @DisplayName("falha quando o vestibular não existe")
        void deveFalharQuandoVestibularNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, new EntranceExam("ENEM", (short) 2026, "1º Dia"), 1L));

            assertEquals("EntranceExam não encontrado: id=404", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a nova instituição não existe")
        void deveFalharQuandoInstituicaoNaoExiste() {
            when(repository.findById(1L)).thenReturn(Optional.of(existente));
            when(institutionRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(1L, new EntranceExam("ENEM", (short) 2026, "1º Dia"), 99L));

            assertEquals("Institution não encontrada: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete e consultas")
    class DeleteEConsultas {

        @Test
        @DisplayName("delete remove o vestibular existente")
        void deveRemoverExistente() {
            when(repository.existsById(1L)).thenReturn(true);

            service.delete(1L);

            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("delete falha quando o vestibular não existe")
        void deveFalharAoRemoverInexistente() {
            when(repository.existsById(404L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("EntranceExam não encontrado: id=404", ex.getMessage());
            verify(repository, never()).deleteById(any());
        }

        @Test
        @DisplayName("findByInstitutionId falha quando a instituição não existe, como os demais filtros")
        void deveFalharAoListarPorInstituicaoInexistente() {
            when(institutionRepository.existsById(99L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findByInstitutionId(99L));

            assertEquals("Institution não encontrada: id=99", ex.getMessage());
            verify(repository, never()).findByInstitutionId(any());
        }
    }
}
