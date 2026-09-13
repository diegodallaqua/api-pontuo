package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.Address;
import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.repository.AddressRepository;
import com.pontuo.api_pontuo.repository.CityRepository;
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
@DisplayName("AddressService")
class AddressServiceTest {

    @Mock
    private AddressRepository repository;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private AddressService service;

    private City brasilia;

    @BeforeEach
    void setUp() {
        brasilia = new City("Brasília");
        brasilia.setId(804L);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("associa o endereço à cidade")
        void deveAssociarCidade() {
            when(cityRepository.findById(804L)).thenReturn(Optional.of(brasilia));
            when(repository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Address criado = service.create(new Address("SIG Quadra 4", "Setor de Indústrias Gráficas", 327, null), 804L);

            assertSame(brasilia, criado.getCity());
        }

        @Test
        @DisplayName("falha quando a cidade não existe")
        void deveFalharQuandoCidadeNaoExiste() {
            when(cityRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(new Address("Rua A", "Centro", 10, null), 99L));

            assertEquals("City não encontrada: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        private Address existente;

        @BeforeEach
        void setUpExistente() {
            existente = new Address("Rua antiga", "Bairro antigo", 1, null, brasilia);
            existente.setId(1L);
        }

        @Test
        @DisplayName("copia todos os campos para o endereço existente")
        void deveAtualizarCampos() {
            City saoPaulo = new City("São Paulo");
            saoPaulo.setId(5285L);
            when(repository.findById(1L)).thenReturn(Optional.of(existente));
            when(cityRepository.findById(5285L)).thenReturn(Optional.of(saoPaulo));
            when(repository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Address resultado = service.update(1L,
                    new Address("Rua dos Andradas", "Santa Ifigênia", 140, "Sala 2"), 5285L);

            assertAll(
                    () -> assertEquals(1L, resultado.getId()),
                    () -> assertEquals("Rua dos Andradas", resultado.getStreet()),
                    () -> assertEquals("Santa Ifigênia", resultado.getNeighborhood()),
                    () -> assertEquals(140, resultado.getNumber()),
                    () -> assertEquals("Sala 2", resultado.getComplement()),
                    () -> assertSame(saoPaulo, resultado.getCity()));
        }

        @Test
        @DisplayName("falha quando o endereço não existe")
        void deveFalharQuandoEnderecoNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, new Address("Rua A", "Centro", 10, null), 804L));

            assertEquals("Address não encontrado: id=404", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a nova cidade não existe")
        void deveFalharQuandoCidadeNaoExiste() {
            when(repository.findById(1L)).thenReturn(Optional.of(existente));
            when(cityRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(1L, new Address("Rua A", "Centro", 10, null), 99L));

            assertEquals("City não encontrada: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete e consultas")
    class DeleteEConsultas {

        @Test
        @DisplayName("delete remove o endereço existente")
        void deveRemoverExistente() {
            when(repository.existsById(1L)).thenReturn(true);

            service.delete(1L);

            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("delete falha com mensagem do endereço, não da cidade")
        void deveFalharAoRemoverInexistente() {
            when(repository.existsById(404L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("Address não encontrado: id=404", ex.getMessage());
            verify(repository, never()).deleteById(any());
        }

        @Test
        @DisplayName("findByCityId falha com mensagem da cidade, que é quem não existe")
        void deveFalharAoListarPorCidadeInexistente() {
            when(cityRepository.existsById(99L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findByCityId(99L));

            assertEquals("City não encontrada: id=99", ex.getMessage());
            verify(repository, never()).findByCityId(any());
        }
    }
}
