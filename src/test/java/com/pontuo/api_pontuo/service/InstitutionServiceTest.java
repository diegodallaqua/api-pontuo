package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.Address;
import com.pontuo.api_pontuo.entity.Institution;
import com.pontuo.api_pontuo.repository.AddressRepository;
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
@DisplayName("InstitutionService")
class InstitutionServiceTest {

    @Mock
    private InstitutionRepository repository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private InstitutionService service;

    private Address sedeInep;

    @BeforeEach
    void setUp() {
        sedeInep = new Address("SIG Quadra 4", "Setor de Indústrias Gráficas", 327, "Sede do Inep");
        sedeInep.setId(1L);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("associa a instituição ao endereço")
        void deveAssociarEndereco() {
            when(addressRepository.findById(1L)).thenReturn(Optional.of(sedeInep));
            when(repository.save(any(Institution.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Institution criada = service.create(new Institution("Instituto Nacional", "INEP"), 1L);

            assertSame(sedeInep, criada.getAddress());
        }

        @Test
        @DisplayName("falha quando o endereço não existe")
        void deveFalharQuandoEnderecoNaoExiste() {
            when(addressRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(new Institution("Instituto Nacional", "INEP"), 99L));

            assertEquals("Address não encontrada: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        private Institution existente;

        @BeforeEach
        void setUpExistente() {
            existente = new Institution("Nome antigo", "ANT", sedeInep);
            existente.setId(1L);
        }

        @Test
        @DisplayName("atualiza nome, sigla e endereço da instituição existente")
        void deveAtualizarCampos() {
            Address outro = new Address("Rua dos Andradas", "Santa Ifigênia", 140, null);
            outro.setId(2L);
            when(repository.findById(1L)).thenReturn(Optional.of(existente));
            when(addressRepository.findById(2L)).thenReturn(Optional.of(outro));
            when(repository.save(any(Institution.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Institution resultado = service.update(1L,
                    new Institution("Fundação Universitária para o Vestibular", "FUVEST"), 2L);

            assertAll(
                    () -> assertEquals(1L, resultado.getId()),
                    () -> assertEquals("Fundação Universitária para o Vestibular", resultado.getName()),
                    () -> assertEquals("FUVEST", resultado.getAcronym()),
                    () -> assertSame(outro, resultado.getAddress()));
        }

        @Test
        @DisplayName("falha quando a instituição não existe")
        void deveFalharQuandoInstituicaoNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, new Institution("Nome", "SIG"), 1L));

            assertEquals("Institution não encontrada: id=404", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando o novo endereço não existe")
        void deveFalharQuandoEnderecoNaoExiste() {
            when(repository.findById(1L)).thenReturn(Optional.of(existente));
            when(addressRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(1L, new Institution("Nome", "SIG"), 99L));

            assertEquals("Address não encontrada: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("remove a instituição existente")
        void deveRemoverExistente() {
            when(repository.existsById(1L)).thenReturn(true);

            service.delete(1L);

            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("falha quando a instituição não existe")
        void deveFalharAoRemoverInexistente() {
            when(repository.existsById(404L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("Institution não encontrada: id=404", ex.getMessage());
            verify(repository, never()).deleteById(any());
        }
    }
}
