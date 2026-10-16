package com.pontuo.api_pontuo.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EntityNotFoundException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalArgumentException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Regra de propriedade violada dentro do service (ex.: estudante abrindo o
     * simulado de outro). A negação por perfil na rota é tratada antes, pelo
     * JsonSecurityErrorWriter.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Restrição do banco violada — em geral apagar um registro ainda
     * referenciado, como uma questão que já está em algum simulado. A mensagem
     * não repete o erro do banco para não expor a estrutura das tabelas.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Operação recusada: o registro está em uso por outros dados ou já existe.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Falha de login. A mensagem é genérica de propósito: dizer se foi o
     * usuário ou a senha que errou ajudaria a enumerar contas válidas.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthentication(AuthenticationException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Credenciais inválidas.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
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
     * Parâmetro de query fora do permitido — page negativo, size acima do teto.
     * Responde no mesmo formato do corpo inválido: uma mensagem por parâmetro.
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleParameterValidation(
            HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ParameterValidationResult result : ex.getParameterValidationResults()) {
            String parameter = result.getMethodParameter().getParameterName();
            for (MessageSourceResolvable error : result.getResolvableErrors()) {
                errors.put(parameter == null ? "request" : parameter, error.getDefaultMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}