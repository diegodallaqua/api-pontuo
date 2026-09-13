package com.pontuo.api_pontuo.support;

import com.pontuo.api_pontuo.config.SecurityConfig;
import com.pontuo.api_pontuo.security.RevokedTokenValidator;
import com.pontuo.api_pontuo.security.TokenRevocationService;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Carrega a configuração de segurança real da API num teste {@code @WebMvcTest}.
 * O segredo fica fixo aqui para o teste não depender do .env, que não existe na CI.
 * A classe de teste ainda precisa fornecer um UserDetailsService (real ou mock),
 * exigido pelo AuthenticationManager do SecurityConfig.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SecurityConfig.class, RevokedTokenValidator.class, TokenRevocationService.class})
@TestPropertySource(properties = {
        "security.jwt.secret=segredo-de-teste-com-mais-de-32-bytes!!",
        "security.jwt.issuer=api-pontuo",
        "security.jwt.expiration-minutes=120"
})
public @interface WithApiSecurity {
}
