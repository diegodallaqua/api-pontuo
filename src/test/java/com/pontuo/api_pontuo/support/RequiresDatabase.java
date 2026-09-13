package com.pontuo.api_pontuo.support;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledIf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Teste que precisa do MariaDB local. Sem banco (por exemplo, na CI) o teste é
 * pulado em vez de falhar.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Tag("integration")
@EnabledIf(value = "com.pontuo.api_pontuo.support.TestDatabase#isAvailable",
        disabledReason = "MariaDB indisponível em localhost:3306")
public @interface RequiresDatabase {
}
