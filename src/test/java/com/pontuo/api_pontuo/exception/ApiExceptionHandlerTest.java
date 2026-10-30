package com.pontuo.api_pontuo.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @DisplayName("AccessDeniedException do service vira 403 com a mensagem da exceção")
    void deveResponder403() {
        ResponseEntity<Map<String, String>> resposta = handler.handleAccessDenied(
                new AccessDeniedException("Acesso negado: o registro pertence a outro usuário."));

        assertAll(
                () -> assertEquals(HttpStatus.FORBIDDEN, resposta.getStatusCode()),
                () -> assertEquals(Map.of("message", "Acesso negado: o registro pertence a outro usuário."),
                        resposta.getBody()));
    }

    @Test
    @DisplayName("violação de restrição do banco vira 409 sem expor o erro SQL")
    void deveResponder409SemDetalheDoBanco() {
        ResponseEntity<Map<String, String>> resposta = handler.handleDataIntegrity(
                new DataIntegrityViolationException("Cannot delete or update a parent row: fk_mehq_question"));

        assertAll(
                () -> assertEquals(HttpStatus.CONFLICT, resposta.getStatusCode()),
                () -> assertEquals(
                        Map.of("message", "Operação recusada: o registro está em uso por outros dados ou já existe."),
                        resposta.getBody()));
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

    @Test
    @DisplayName("id que não converte para número vira 400 dizendo o tipo esperado")
    void deveResponder400ParaTipoInvalido() throws NoSuchMethodException {
        MethodParameter parametro = new MethodParameter(
                ApiExceptionHandlerTest.class.getDeclaredMethod("endpointComId", Long.class), 0);

        ResponseEntity<Map<String, String>> resposta = handler.handleTypeMismatch(
                new MethodArgumentTypeMismatchException("abc", Long.class, "id", parametro, null));

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode()),
                () -> assertEquals(Map.of("id", "id deve ser um número inteiro (recebido: abc)"),
                        resposta.getBody()));
    }

    @Test
    @DisplayName("parâmetro obrigatório ausente vira 400 com o nome do parâmetro")
    void deveResponder400ParaParametroAusente() {
        ResponseEntity<Map<String, String>> resposta = handler.handleMissingParameter(
                new MissingServletRequestParameterException("size", "int"));

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode()),
                () -> assertEquals(Map.of("size", "size é obrigatório"), resposta.getBody()));
    }

    @Test
    @DisplayName("rota inexistente vira 404 com o método e o caminho pedidos")
    void deveResponder404ParaRotaInexistente() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/enderecos");

        ResponseEntity<Map<String, String>> resposta = handler.handleNoRoute(
                new NoResourceFoundException(HttpMethod.GET, "/api/enderecos", "api/enderecos"), request);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode()),
                () -> assertEquals(Map.of("message", "Rota não encontrada: GET /api/enderecos"),
                        resposta.getBody()));
    }

    @Test
    @DisplayName("método não suportado vira 405 listando os aceitos e com o header Allow")
    void deveResponder405() {
        ResponseEntity<Map<String, String>> resposta = handler.handleMethodNotSupported(
                new HttpRequestMethodNotSupportedException("PATCH", List.of("GET", "PUT")));

        assertAll(
                () -> assertEquals(HttpStatus.METHOD_NOT_ALLOWED, resposta.getStatusCode()),
                () -> assertEquals(
                        Map.of("message", "Método PATCH não suportado nesta rota. Métodos aceitos: GET, PUT."),
                        resposta.getBody()),
                () -> assertEquals(Set.of(HttpMethod.GET, HttpMethod.PUT), resposta.getHeaders().getAllow()));
    }

    @Test
    @DisplayName("erro inesperado vira 500 genérico, sem expor o detalhe interno")
    void deveResponder500SemDetalhe() {
        ResponseEntity<Map<String, String>> resposta =
                handler.handleUnexpected(new NullPointerException("campo x nulo"));

        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode()),
                () -> assertEquals(Map.of("message", "Erro interno inesperado. Tente novamente mais tarde."),
                        resposta.getBody()));
    }

    @SuppressWarnings("unused")
    private void endpointFicticio(Object requestDTO) {
    }

    @SuppressWarnings("unused")
    private void endpointComId(Long id) {
    }
}
