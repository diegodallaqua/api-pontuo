/**
 * Configuração da aplicação.
 *
 * <p>{@link com.pontuo.api_pontuo.config.SecurityConfig} concentra a criação
 * dos objetos de infraestrutura de segurança. Cada método {@code @Bean} é
 * chamado uma vez pelo contêiner, e o objeto devolvido é reaproveitado em toda
 * a aplicação — <b>Singleton</b> com a construção declarada em um só lugar, em
 * vez de espalhada por {@code new} nas classes que precisam do objeto.</p>
 *
 * <p>Concentrar a construção aqui também é o que permite trocar uma estratégia
 * inteira num ponto só: o {@code PasswordEncoder} e os validadores do
 * {@code JwtDecoder} mudam editando o {@code @Bean} correspondente, sem tocar
 * nos services nem nos controllers.</p>
 */
package com.pontuo.api_pontuo.config;
