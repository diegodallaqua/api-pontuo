package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.QuestionImage;
import com.pontuo.api_pontuo.service.ImageStorageService;
import com.pontuo.api_pontuo.service.PresignedUpload;
import com.pontuo.api_pontuo.service.QuestionImageService;
import com.pontuo.api_pontuo.support.WithApiSecurity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionImageController.class)
@WithApiSecurity
class QuestionImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuestionImageService service;

    @MockitoBean
    private ImageStorageService storage;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void estudanteNaoRecebeExplicacaoNaQuestaoDaImagem() throws Exception {
        Question question = new Question("Enunciado", "Explicação", (short) 2);
        question.setId(7L);
        QuestionImage image = new QuestionImage("https://exemplo/imagem.png", "Legenda", question);
        image.setId(1L);
        when(service.findById(1L)).thenReturn(Optional.of(image));

        mockMvc.perform(get("/api/question-images/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ESTUDANTE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question.explanation").doesNotExist());
        mockMvc.perform(get("/api/question-images/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question.explanation").value("Explicação"));
    }

    @Test
    void administradorRecebeUrlAssinadaParaUpload() throws Exception {
        when(storage.presignUpload("image/png", 2048L)).thenReturn(new PresignedUpload(
                "https://conta.r2.cloudflarestorage.com/bucket/question-images/abc.png?X-Amz-Signature=x",
                Map.of("Content-Type", "image/png"),
                "https://img.pontuo.app/question-images/abc.png",
                Instant.parse("2026-11-05T12:10:00Z")));

        mockMvc.perform(post("/api/question-images/upload-url").with(administrador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contentType\": \"image/png\", \"size\": 2048}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uploadUrl").value(
                        "https://conta.r2.cloudflarestorage.com/bucket/question-images/abc.png?X-Amz-Signature=x"))
                .andExpect(jsonPath("$.headers['Content-Type']").value("image/png"))
                .andExpect(jsonPath("$.url").value("https://img.pontuo.app/question-images/abc.png"))
                .andExpect(jsonPath("$.expiresAt").value("2026-11-05T12:10:00Z"));
    }

    @Test
    void uploadRecusaTipoForaDaListaETamanhoAcimaDoLimite() throws Exception {
        mockMvc.perform(post("/api/question-images/upload-url").with(administrador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contentType\": \"image/svg+xml\", \"size\": 2048}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.contentType")
                        .value("contentType deve ser image/png, image/jpeg, image/webp ou image/gif"));
        mockMvc.perform(post("/api/question-images/upload-url").with(administrador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contentType\": \"image/png\", \"size\": 5242881}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size").value("size deve ser no máximo 5242880 bytes (5 MB)"));
        verifyNoInteractions(storage);
    }

    @Test
    void estudanteNaoPedeUrlDeUpload() throws Exception {
        mockMvc.perform(post("/api/question-images/upload-url")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ESTUDANTE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contentType\": \"image/png\", \"size\": 2048}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(storage);
    }

    private static JwtRequestPostProcessor administrador() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
    }
}
