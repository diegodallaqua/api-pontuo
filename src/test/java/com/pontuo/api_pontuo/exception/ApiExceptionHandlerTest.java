package com.pontuo.api_pontuo.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ApiExceptionHandler")
class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    @DisplayName("EntityNotFoundException vira 404 com a mensagem da exceção")
    void deveResponder404() {
        ResponseEntity<Map<String, String>> resposta =
                handler.handleNotFound(new EntityNotFoundException("State não encontrada: id=99"));

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode()),
                () -> assertEquals(Map.of("message", "State não encontrada: id=99"), resposta.getBody()));
    }

    @Test
    @DisplayName("IllegalArgumentException vira 409 com a mensagem da exceção")
    void deveResponder409() {
        ResponseEntity<Map<String, String>> resposta =
                handler.handleConflict(new IllegalArgumentException("username já cadastrado: maria"));

        assertAll(
                () -> assertEquals(HttpStatus.CONFLICT, resposta.getStatusCode()),
                () -> assertEquals(Map.of("message", "username já cadastrado: maria"), resposta.getBody()));
    }

    @Test
    @DisplayName("falha de login vira 401 genérico, sem o detalhe que ajudaria a enumerar contas")
    void deveResponder401SemDetalhe() {
        ResponseEntity<Map<String, String>> resposta =
                handler.handleAuthentication(new BadCredentialsException("usuário maria não existe"));

        assertAll(
                () -> assertEquals(HttpStatus.UNAUTHORIZED, resposta.getStatusCode()),
                () -> assertEquals(Map.of("message", "Credenciais inválidas."), resposta.getBody()));
    }

    @Test
    @DisplayName("falha de validação vira 400 com uma mensagem por campo")
    void deveResponder400ComErrosPorCampo() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "requestDTO");
        bindingResult.addError(new FieldError("requestDTO", "username", "username é obrigatório"));
        bindingResult.addError(new FieldError("requestDTO", "password", "password deve ter entre 8 e 72 caracteres"));
        MethodParameter parametro = new MethodParameter(
                ApiExceptionHandlerTest.class.getDeclaredMethod("endpointFicticio", Object.class), 0);

        ResponseEntity<Map<String, String>> resposta =
                handler.handleValidation(new MethodArgumentNotValidException(parametro, bindingResult));

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode()),
                () -> assertEquals(Map.of(
                        "username", "username é obrigatório",
                        "password", "password deve ter entre 8 e 72 caracteres"), resposta.getBody()));
    }

    @SuppressWarnings("unused")
    private void endpointFicticio(Object requestDTO) {
    }
}
