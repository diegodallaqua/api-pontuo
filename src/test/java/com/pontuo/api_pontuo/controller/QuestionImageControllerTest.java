package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.QuestionImage;
import com.pontuo.api_pontuo.service.QuestionImageService;
import com.pontuo.api_pontuo.support.WithApiSecurity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
