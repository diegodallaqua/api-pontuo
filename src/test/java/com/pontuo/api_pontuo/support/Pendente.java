package com.pontuo.api_pontuo.support;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Teste escrito antes da implementação (etapa "red" do TDD). Fica desligado na
 * execução normal para não quebrar o build e roda com:
 *
 * <pre>mvnw test -Dpontuo.pendentes=true</pre>
 *
 * Quando a funcionalidade estiver pronta e o teste passar, remova a anotação.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Tag("pendente")
@EnabledIfSystemProperty(named = "pontuo.pendentes", matches = "true",
        disabledReason = "comportamento ainda não implementado (rode com -Dpontuo.pendentes=true)")
public @interface Pendente {

    /** O que falta implementar para o teste passar. */
    String value();
}
