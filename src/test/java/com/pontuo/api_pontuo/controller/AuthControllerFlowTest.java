package com.pontuo.api_pontuo.controller;

import com.jayway.jsonpath.JsonPath;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.entity.UserRole;
import com.pontuo.api_pontuo.repository.UserRepository;
import com.pontuo.api_pontuo.repository.UserRoleRepository;
import com.pontuo.api_pontuo.security.AppUserDetailsService;
import com.pontuo.api_pontuo.security.JwtService;
import com.pontuo.api_pontuo.service.UserService;
import com.pontuo.api_pontuo.support.WithApiSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, UserController.class})
@WithApiSecurity
@Import({UserService.class, AppUserDetailsService.class, JwtService.class})
@DisplayName("Fluxo de autenticação")
class AuthControllerFlowTest {

    private static final String CADASTRO_JSON = """
            {"username": "maria", "email": "maria@pontuo.com", "birthDate": "2001-03-09", "password": "SenhaForte123"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserRoleRepository userRoleRepository;

    private final Map<String, User> usuariosPorUsername = new HashMap<>();

    @BeforeEach
    void simularBanco() {
        UserRole administrador = new UserRole("Administrador");
        administrador.setId(1L);
        UserRole estudante = new UserRole("Estudante");
        estudante.setId(2L);
        when(userRoleRepository.findById(1L)).thenReturn(Optional.of(administrador));
        when(userRoleRepository.findById(2L)).thenReturn(Optional.of(estudante));
        when(userRoleRepository.findByDescription("Estudante")).thenReturn(Optional.of(estudante));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if (user.getId() == null) {
                user.setId(usuariosPorUsername.size() + 1L);
            }
            usuariosPorUsername.put(user.getUsername(), user);
            return user;
        });
        when(userRepository.existsByUsername(anyString()))
                .thenAnswer(invocation -> usuariosPorUsername.containsKey(invocation.<String>getArgument(0)));
        when(userRepository.existsByEmail(anyString()))
                .thenAnswer(invocation -> buscarPorEmail(invocation.getArgument(0)).isPresent());
        when(userRepository.findByUsername(anyString()))
                .thenAnswer(invocation -> Optional.ofNullable(usuariosPorUsername.get(invocation.<String>getArgument(0))));
        when(userRepository.findByEmail(anyString()))
                .thenAnswer(invocation -> buscarPorEmail(invocation.getArgument(0)));
        when(userRepository.findAll())
                .thenAnswer(invocation -> List.copyOf(usuariosPorUsername.values()));
    }

    private Optional<User> buscarPorEmail(String email) {
        return usuariosPorUsername.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Test
    @DisplayName("cadastra, faz login, consulta /me, sai e o mesmo token passa a ser recusado")
    void deveCumprirOCicloDeVidaDoToken() throws Exception {
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(CADASTRO_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userRole.description").value("Estudante"))
                .andExpect(jsonPath("$.password").doesNotExist());

        String respostaDoLogin = login("maria", "SenhaForte123")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(7200))
                .andExpect(jsonPath("$.user.username").value("maria"))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        String bearer = "Bearer " + JsonPath.read(respostaDoLogin, "$.accessToken");

        mockMvc.perform(get("/api/auth/me").header(HttpHeaders.AUTHORIZATION, bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("maria"))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(post("/api/auth/logout").header(HttpHeaders.AUTHORIZATION, bearer))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/auth/me").header(HttpHeaders.AUTHORIZATION, bearer))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("grava a senha como hash BCrypt, nunca em texto puro")
    void deveGravarSenhaComHash() throws Exception {
        cadastrarMaria();

        String senhaGravada = usuariosPorUsername.get("maria").getPassword();
        assertAll(
                () -> assertNotEquals("SenhaForte123", senhaGravada),
                () -> assertTrue(senhaGravada.startsWith("$2"), "esperado hash BCrypt, veio " + senhaGravada));
    }

    @Test
    @DisplayName("cadastro público ignora userRoleId enviado no corpo e cria estudante")
    void naoDeveCriarAdministradorPeloCadastroPublico() throws Exception {
        String cadastroPedindoAdmin = """
                {"username": "maria", "email": "maria@pontuo.com", "birthDate": "2001-03-09",
                 "password": "SenhaForte123", "userRoleId": 1}
                """;

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(cadastroPedindoAdmin))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userRole.description").value("Estudante"));
    }

    @Test
    @DisplayName("aceita o email no lugar do username no login")
    void deveLogarComEmail() throws Exception {
        cadastrarMaria();

        login("maria@pontuo.com", "SenhaForte123")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("maria"));
    }

    @Test
    @DisplayName("senha errada e usuário inexistente recebem a mesma resposta")
    void deveResponderIgualParaQualquerFalhaDeLogin() throws Exception {
        cadastrarMaria();

        String senhaErrada = login("maria", "SenhaErrada123")
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();
        String usuarioInexistente = login("ninguem", "SenhaForte123")
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertAll(
                () -> assertEquals(senhaErrada, usuarioInexistente),
                () -> assertEquals("Credenciais inválidas.", JsonPath.read(senhaErrada, "$.message")));
    }

    @Test
    @DisplayName("/me responde 404 quando o usuário do token foi removido depois do login")
    void deveResponder404ParaUsuarioRemovido() throws Exception {
        cadastrarMaria();
        String resposta = login("maria", "SenhaForte123").andReturn().getResponse().getContentAsString();
        usuariosPorUsername.remove("maria");

        mockMvc.perform(get("/api/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + JsonPath.read(resposta, "$.accessToken")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User não encontrado: username=maria"));
    }

    @Test
    @DisplayName("listagem de usuários não expõe a senha")
    void naoDeveExporSenhaNaListagem() throws Exception {
        cadastrarMaria();

        mockMvc.perform(get("/api/users").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("maria"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    private void cadastrarMaria() throws Exception {
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(CADASTRO_JSON))
                .andExpect(status().isCreated());
    }

    private ResultActions login(String username, String password) throws Exception {
        String corpo = "{\"username\": \"%s\", \"password\": \"%s\"}".formatted(username, password);
        return mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(corpo));
    }
}
