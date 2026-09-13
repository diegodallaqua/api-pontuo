package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.repository.CityRepository;
import com.pontuo.api_pontuo.repository.StateRepository;
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
@DisplayName("CityService")
class CityServiceTest {

    @Mock
    private CityRepository repository;

    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private CityService service;

    private State acre;

    @BeforeEach
    void setUp() {
        acre = new State("Acre");
        acre.setId(1L);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("associa a cidade ao estado informado")
        void deveAssociarEstado() {
            when(stateRepository.findById(1L)).thenReturn(Optional.of(acre));
            when(repository.save(any(City.class))).thenAnswer(invocation -> invocation.getArgument(0));

            City criada = service.create(new City("Rio Branco"), 1L);

            assertSame(acre, criada.getState());
        }

        @Test
        @DisplayName("falha quando o estado não existe")
        void deveFalharQuandoEstadoNaoExiste() {
            when(stateRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(new City("Rio Branco"), 99L));

            assertEquals("State não encontrado: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        private City existente;

        @BeforeEach
        void setUpExistente() {
            existente = new City("Rio Branco", acre);
            existente.setId(16L);
        }

        @Test
        @DisplayName("atualiza nome e estado da cidade existente")
        void deveAtualizarCampos() {
            State alagoas = new State("Alagoas");
            alagoas.setId(2L);
            when(repository.findById(16L)).thenReturn(Optional.of(existente));
            when(stateRepository.findById(2L)).thenReturn(Optional.of(alagoas));
            when(repository.save(any(City.class))).thenAnswer(invocation -> invocation.getArgument(0));

            City resultado = service.update(16L, new City("Maceió"), 2L);

            assertAll(
                    () -> assertEquals(16L, resultado.getId()),
                    () -> assertEquals("Maceió", resultado.getName()),
                    () -> assertSame(alagoas, resultado.getState()));
        }

        @Test
        @DisplayName("falha quando a cidade não existe")
        void deveFalharQuandoCidadeNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, new City("Maceió"), 1L));

            assertEquals("City não encontrada: id=404", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando o novo estado não existe")
        void deveFalharQuandoEstadoNaoExiste() {
            when(repository.findById(16L)).thenReturn(Optional.of(existente));
            when(stateRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(16L, new City("Maceió"), 99L));

            assertEquals("State não encontrado: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete e consultas")
    class DeleteEConsultas {

        @Test
        @DisplayName("delete remove a cidade existente")
        void deveRemoverExistente() {
            when(repository.existsById(16L)).thenReturn(true);

            service.delete(16L);

            verify(repository).deleteById(16L);
        }

        @Test
        @DisplayName("delete falha quando a cidade não existe")
        void deveFalharAoRemoverInexistente() {
            when(repository.existsById(404L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("City não encontrada: id=404", ex.getMessage());
            verify(repository, never()).deleteById(any());
        }

        @Test
        @DisplayName("findByStateId falha quando o estado não existe")
        void deveFalharAoListarPorEstadoInexistente() {
            when(stateRepository.existsById(99L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findByStateId(99L));

            assertEquals("State não encontrado: id=99", ex.getMessage());
            verify(repository, never()).findByStateId(any());
        }
    }
}
