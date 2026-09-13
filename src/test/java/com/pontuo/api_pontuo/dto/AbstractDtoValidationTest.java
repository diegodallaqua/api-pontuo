package com.pontuo.api_pontuo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.stream.Collectors;


abstract class AbstractDtoValidationTest {

    private static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-09-12T12:00:00Z"), ZoneOffset.UTC);

    protected static final LocalDate HOJE = LocalDate.now(RELOGIO);

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void criarValidator() {
        factory = Validation.byDefaultProvider()
                .configure()
                .clockProvider(() -> RELOGIO)
                .buildValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void fecharValidator() {
        factory.close();
    }

    protected static Set<ConstraintViolation<Object>> violacoes(Object dto) {
        return validator.validate(dto);
    }

    protected static Set<String> mensagens(Object dto, String campo) {
        return violacoes(dto).stream()
                .filter(violacao -> violacao.getPropertyPath().toString().equals(campo))
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}
