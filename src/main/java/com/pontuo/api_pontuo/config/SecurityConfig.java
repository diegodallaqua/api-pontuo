package com.pontuo.api_pontuo.config;

import com.pontuo.api_pontuo.security.JsonSecurityErrorWriter;
import com.pontuo.api_pontuo.security.JwtService;
import com.pontuo.api_pontuo.security.RevokedTokenValidator;
import com.pontuo.api_pontuo.security.Roles;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class SecurityConfig {

    /** Tamanho mínimo exigido para a chave HS256 (256 bits). */
    private static final int MIN_SECRET_BYTES = 32;

    /** Tempo que o navegador pode guardar a resposta do preflight. */
    private static final long CORS_MAX_AGE_SECONDS = 3600;

    /** Rotas de leitura e escrita liberadas para qualquer usuário autenticado. */
    private static final String[] STUDENT_WRITABLE = {
            "/api/mock-exams/**",
            "/api/mock-exam-questions/**"
    };

    /**
     * A API é stateless: nada de sessão ou cookie, a identidade vem apenas do
     * Bearer token. Por isso o CSRF pode ser desligado sem abrir brecha — não
     * existe credencial que o navegador envie automaticamente.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtDecoder jwtDecoder,
                                           JwtAuthenticationConverter jwtAuthenticationConverter,
                                           CorsConfigurationSource corsConfigurationSource)
            throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // Únicas rotas públicas: obter um token e criar conta de estudante.
                    .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register")
                            .permitAll()
                    // Precisam de token, mas de nenhuma role específica.
                    .requestMatchers("/api/auth/logout", "/api/auth/me").authenticated()
                    // Gestão de usuários e perfis é exclusiva do administrador.
                    .requestMatchers("/api/users/**", "/api/user-roles/**").hasRole(Roles.ADMINISTRADOR)
                    // Desempenho de outra pessoa só para o administrador; o
                    // /api/performance/me/** cai na leitura autenticada abaixo.
                    .requestMatchers("/api/performance/users/**").hasRole(Roles.ADMINISTRADOR)
                    .requestMatchers(STUDENT_WRITABLE).authenticated()
                    // Conteúdo do catálogo: qualquer autenticado lê, só admin altera.
                    .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/**").hasRole(Roles.ADMINISTRADOR)
                    .requestMatchers(HttpMethod.PUT, "/api/**").hasRole(Roles.ADMINISTRADOR)
                    .requestMatchers(HttpMethod.PATCH, "/api/**").hasRole(Roles.ADMINISTRADOR)
                    .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole(Roles.ADMINISTRADOR)
                    .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                    .decoder(jwtDecoder)
                    .jwtAuthenticationConverter(jwtAuthenticationConverter)))
            .exceptionHandling(handling -> handling
                    .authenticationEntryPoint(JsonSecurityErrorWriter.entryPoint())
                    .accessDeniedHandler(JsonSecurityErrorWriter.accessDeniedHandler()));
        return http.build();
    }

    /**
     * Sem isto o navegador bloqueia qualquer chamada de um frontend em outra
     * origem — que é o caso do Flutter Web. As origens liberadas vêm da
     * configuração; o preflight (OPTIONS) é respondido aqui, antes das regras
     * de autorização, então não exige token. allowCredentials fica desligado
     * de propósito: a identidade vem do header Authorization, não de cookie.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${security.cors.allowed-origins}") List<String> allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        // Patterns, e não origens fixas, porque o Flutter Web em modo debug
        // sobe numa porta aleatória de localhost.
        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()));
        configuration.setAllowedHeaders(List.of(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT));
        // O cliente precisa ler este header para saber que o token caiu.
        configuration.setExposedHeaders(List.of(HttpHeaders.WWW_AUTHENTICATE));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(CORS_MAX_AGE_SECONDS);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    /**
     * Chave simétrica de assinatura. Falhar no startup é intencional: é melhor
     * a aplicação não subir do que subir com uma chave fraca ou default.
     */
    @Bean
    public SecretKey jwtSecretKey(@Value("${security.jwt.secret}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "security.jwt.secret deve ter no mínimo " + MIN_SECRET_BYTES + " caracteres.");
        }
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey,
                                 @Value("${security.jwt.issuer}") String issuer,
                                 RevokedTokenValidator revokedTokenValidator) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        // Além de expiração e emissor, recusa tokens já invalidados pelo logout.
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer),
                revokedTokenValidator));
        return decoder;
    }

    /** Traduz o claim de roles do token em authorities ROLE_*. */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName(JwtService.ROLES_CLAIM);
        authorities.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        converter.setPrincipalClaimName("sub");
        return converter;
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    // Usado para gravar a senha do usuário com hash, nunca em texto puro.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
