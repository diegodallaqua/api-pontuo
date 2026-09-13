package com.pontuo.api_pontuo.security;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("JsonSecurityErrorWriter")
class JsonSecurityErrorWriterTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("sem autenticação responde 401 em JSON com o header WWW-Authenticate")
    void deveResponder401EmJson() throws Exception {
        JsonSecurityErrorWriter.entryPoint()
                .commence(request, response, new BadCredentialsException("Jwt expired at 2026-09-12"));

        assertAll(
                () -> assertEquals(401, response.getStatus()),
                () -> assertEquals("Bearer", response.getHeader(HttpHeaders.WWW_AUTHENTICATE)),
                () -> assertTrue(response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)),
                () -> assertEquals("UTF-8", response.getCharacterEncoding()),
                () -> assertEquals("Autenticação necessária: envie um token válido no header Authorization.",
                        mensagem(response)),
                () -> assertFalse(response.getContentAsString().contains("expired"),
                        "o detalhe da exceção não deve vazar para o cliente"));
    }

    @Test
    @DisplayName("sem permissão responde 403 no mesmo formato JSON")
    void deveResponder403EmJson() throws Exception {
        JsonSecurityErrorWriter.accessDeniedHandler()
                .handle(request, response, new AccessDeniedException("Access Denied"));

        assertAll(
                () -> assertEquals(403, response.getStatus()),
                () -> assertTrue(response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)),
                () -> assertEquals("Acesso negado: seu perfil não tem permissão para este recurso.",
                        mensagem(response)));
    }

    // JsonPath falha se o corpo não for um JSON válido, o que também é verificado aqui.
    private static String mensagem(MockHttpServletResponse response) throws UnsupportedEncodingException {
        return JsonPath.read(response.getContentAsString(), "$.message");
    }
}
