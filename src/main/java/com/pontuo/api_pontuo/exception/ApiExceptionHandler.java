package com.pontuo.api_pontuo.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converte as exceções da API em respostas JSON com uma mensagem que explique
 * ao cliente o que deu errado. Dois formatos convivem: {@code {"message": "..."}}
 * para erros da requisição como um todo, e uma mensagem por campo ou parâmetro
 * ({@code {"id": "id deve ser maior que zero"}}) para erros de validação.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EntityNotFoundException ex) {
        return message(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalArgumentException ex) {
        return message(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Regra de propriedade violada dentro do service (ex.: estudante abrindo o
     * simulado de outro). A negação por perfil na rota é tratada antes, pelo
     * JsonSecurityErrorWriter.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return message(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /**
     * Restrição do banco violada — em geral apagar um registro ainda
     * referenciado, como uma questão que já está em algum simulado. A mensagem
     * não repete o erro do banco para não expor a estrutura das tabelas.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return message(HttpStatus.CONFLICT,
                "Operação recusada: o registro está em uso por outros dados ou já existe.");
    }

    /**
     * Falha de login. A mensagem é genérica de propósito: dizer se foi o
     * usuário ou a senha que errou ajudaria a enumerar contas válidas.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthentication(AuthenticationException ex) {
        return message(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Parâmetro de path ou de query fora do permitido — id zero ou negativo,
     * page negativo, size acima do teto. Responde no mesmo formato do corpo
     * inválido: uma mensagem por parâmetro. Quando a rota também recebe um
     * corpo com {@code @Valid}, os erros dele chegam por aqui junto com os do
     * id, e cada campo do corpo ganha a sua própria chave.
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleParameterValidation(
            HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ParameterValidationResult result : ex.getParameterValidationResults()) {
            if (result instanceof ParameterErrors bodyErrors) {
                for (FieldError fieldError : bodyErrors.getFieldErrors()) {
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage());
                }
                continue;
            }
            String parameter = result.getMethodParameter().getParameterName();
            for (MessageSourceResolvable error : result.getResolvableErrors()) {
                errors.put(parameter == null ? "request" : parameter, error.getDefaultMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /** Valor que não converte para o tipo do parâmetro, como {@code /api/addresses/abc}. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getName(), ex.getName() + " deve ser " + expectedType(ex.getRequiredType())
                + " (recebido: " + ex.getValue() + ")");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParameter(
            MissingServletRequestParameterException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getParameterName(), ex.getParameterName() + " é obrigatório");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /** Corpo ausente, JSON malformado ou campo com tipo errado (texto onde se espera número). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return message(HttpStatus.BAD_REQUEST,
                "Corpo da requisição ausente ou inválido: envie um JSON bem formado, "
                        + "com cada campo no tipo esperado.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoRoute(NoResourceFoundException ex,
                                                             HttpServletRequest request) {
        return message(HttpStatus.NOT_FOUND,
                "Rota não encontrada: " + request.getMethod() + " " + request.getRequestURI());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {
        String supported = ex.getSupportedHttpMethods() == null
                ? ""
                : ex.getSupportedHttpMethods().stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
        Map<String, String> body = new HashMap<>();
        body.put("message", "Método " + ex.getMethod() + " não suportado nesta rota."
                + (supported.isEmpty() ? "" : " Métodos aceitos: " + supported + "."));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .headers(ex.getHeaders())
                .body(body);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex) {
        return message(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Content-Type " + (ex.getContentType() == null ? "ausente" : ex.getContentType())
                        + " não suportado. Envie o corpo como application/json.");
    }

    /**
     * Rede de segurança para o que não foi previsto acima. Exceções do Spring
     * que já carregam um status (ex.: 406) mantêm esse status; qualquer outra
     * vira 500 com mensagem genérica, sem expor detalhes internos, e é
     * registrada no log para investigação.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception ex) {
        if (ex instanceof ErrorResponse errorResponse) {
            String detail = errorResponse.getBody().getDetail();
            return ResponseEntity.status(errorResponse.getStatusCode())
                    .headers(errorResponse.getHeaders())
                    .body(Map.of("message", detail == null ? "Requisição inválida." : detail));
        }
        log.error("Erro inesperado ao processar a requisição", ex);
        return message(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno inesperado. Tente novamente mais tarde.");
    }

    private static ResponseEntity<Map<String, String>> message(HttpStatus status, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }

    private static String expectedType(Class<?> type) {
        if (type == Long.class || type == long.class || type == Integer.class || type == int.class
                || type == Short.class || type == short.class) {
            return "um número inteiro";
        }
        if (type == LocalDate.class) {
            return "uma data no formato AAAA-MM-DD";
        }
        return "um valor válido";
    }
}
