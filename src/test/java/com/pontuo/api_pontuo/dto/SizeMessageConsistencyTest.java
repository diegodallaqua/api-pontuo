package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pega o erro de copiar e colar em que o limite muda na anotação e a mensagem
 * continua com o número antigo, como em @Size(max = 50, message = "...120...").
 */
@DisplayName("Mensagens de @Size")
class SizeMessageConsistencyTest {

    @ParameterizedTest(name = "{0}")
    @DisplayName("citam os mesmos limites configurados na anotação")
    @ValueSource(classes = {
            AddressRequestDTO.class,
            AnswerOptionRequestDTO.class,
            CityRequestDTO.class,
            EntranceExamRequestDTO.class,
            InstitutionRequestDTO.class,
            KnowledgeAreaRequestDTO.class,
            LoginRequestDTO.class,
            MockExamHasQuestionRequestDTO.class,
            MockExamRequestDTO.class,
            QuestionImageRequestDTO.class,
            QuestionRequestDTO.class,
            RegisterRequestDTO.class,
            StateRequestDTO.class,
            SubjectRequestDTO.class,
            TopicRequestDTO.class,
            UserRequestDTO.class,
            UserRoleRequestDTO.class
    })
    void deveCitarLimitesNaMensagem(Class<? extends Record> dto) {
        assertAll(Arrays.stream(dto.getRecordComponents())
                .map(componente -> campo(dto, componente.getName()))
                .filter(campo -> campo.isAnnotationPresent(Size.class))
                .map(campo -> (Executable) () -> verificarMensagem(dto, campo)));
    }

    private static void verificarMensagem(Class<?> dto, Field campo) {
        Size size = campo.getAnnotation(Size.class);
        String onde = dto.getSimpleName() + "." + campo.getName() + ": \"" + size.message() + "\"";

        assertTrue(citaNumero(size.message(), size.max()), onde + " não cita max=" + size.max());
        if (size.min() > 0) {
            assertTrue(citaNumero(size.message(), size.min()), onde + " não cita min=" + size.min());
        }
    }

    // Borda de palavra para "50" não casar dentro de "150".
    private static boolean citaNumero(String mensagem, int numero) {
        return Pattern.compile("\\b" + numero + "\\b").matcher(mensagem).find();
    }

    private static Field campo(Class<?> dto, String nome) {
        try {
            return dto.getDeclaredField(nome);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }
}
