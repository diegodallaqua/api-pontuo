/**
 * Emissão, validação e revogação dos tokens JWT.
 *
 * <p>É aqui que o padrão <b>Strategy</b> aparece de fato no projeto: o Spring
 * Security define pontos de extensão como interfaces e chama a implementação
 * registrada, sem conhecê-la.</p>
 *
 * <ul>
 *   <li>{@link com.pontuo.api_pontuo.security.AppUserDetailsService} implementa
 *       {@code UserDetailsService}, a estratégia de carregar o usuário no
 *       login. A daqui busca por username ou email no banco; trocá-la por LDAP
 *       ou por um provedor externo não exigiria mudança no fluxo de
 *       autenticação.</li>
 *   <li>{@link com.pontuo.api_pontuo.security.RevokedTokenValidator} implementa
 *       {@code OAuth2TokenValidator<Jwt>}, uma estratégia de validação
 *       adicional. O {@code JwtDecoder} combina várias delas com
 *       {@code DelegatingOAuth2TokenValidator} — expiração, emissor e esta, que
 *       recusa tokens invalidados pelo logout.</li>
 *   <li>{@code PasswordEncoder} é outra estratégia: o algoritmo de hash da
 *       senha é escolhido em um único {@code @Bean} de
 *       {@link com.pontuo.api_pontuo.config.SecurityConfig} (hoje BCrypt).</li>
 * </ul>
 *
 * <p>Todos são beans singleton do contêiner, como os demais componentes.</p>
 */
package com.pontuo.api_pontuo.security;
