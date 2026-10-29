package com.pontuo.api_pontuo.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A especificação do Swagger é escrita à mão, então nada impede que ela fique
 * para trás quando um endpoint muda. Este teste compara as rotas declaradas
 * nos controllers com as documentadas em static/openapi.yaml.
 */
@DisplayName("Especificação OpenAPI")
class OpenApiSpecTest {

    private static final String SPEC = "/static/openapi.yaml";
    private static final Set<String> HTTP_METHODS = Set.of("get", "post", "put", "patch", "delete");

    private static Map<String, Map<String, Map<String, Object>>> paths;

    @BeforeAll
    @SuppressWarnings("unchecked")
    static void carregarEspecificacao() throws IOException {
        try (InputStream input = OpenApiSpecTest.class.getResourceAsStream(SPEC)) {
            assertNotNull(input, SPEC + " não encontrado no classpath");
            Map<String, Object> spec = new Yaml().load(input);
            paths = (Map<String, Map<String, Map<String, Object>>>) spec.get("paths");
        }
    }

    @Test
    @DisplayName("documenta exatamente as rotas dos controllers")
    void deveDocumentarTodasAsRotas() throws ClassNotFoundException {
        assertEquals(rotasDosControllers(), rotasDocumentadas());
    }

    @Test
    @DisplayName("cada operação tem resumo, exemplo cURL e status codes")
    void deveDetalharCadaOperacao() {
        assertAll(paths.entrySet().stream()
                .flatMap(path -> path.getValue().entrySet().stream()
                        .filter(operation -> HTTP_METHODS.contains(operation.getKey()))
                        .map(operation -> (Executable) () -> {
                            String onde = operation.getKey().toUpperCase() + " " + path.getKey();
                            Map<String, Object> details = operation.getValue();
                            assertNotNull(details.get("summary"), onde + " sem summary");
                            assertTrue(String.valueOf(details.get("description")).contains("curl -X "),
                                    onde + " sem exemplo cURL");
                            assertTrue(details.get("responses") instanceof Map<?, ?> responses
                                            && !responses.isEmpty(),
                                    onde + " sem responses");
                        })));
    }

    private static Set<String> rotasDocumentadas() {
        Set<String> rotas = new TreeSet<>();
        paths.forEach((path, operations) -> operations.keySet().stream()
                .filter(HTTP_METHODS::contains)
                .forEach(method -> rotas.add(method.toUpperCase() + " " + path)));
        return rotas;
    }

    private static Set<String> rotasDosControllers() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

        Set<String> rotas = new TreeSet<>();
        for (var candidate : scanner.findCandidateComponents(OpenApiSpecTest.class.getPackageName())) {
            Class<?> controller = Class.forName(candidate.getBeanClassName());
            RequestMapping base = AnnotatedElementUtils.findMergedAnnotation(controller, RequestMapping.class);
            String prefix = base == null || base.path().length == 0 ? "" : base.path()[0];

            for (Method method : controller.getDeclaredMethods()) {
                RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
                if (mapping == null) {
                    continue;
                }
                String[] subPaths = mapping.path().length == 0 ? new String[] {""} : mapping.path();
                for (var httpMethod : mapping.method()) {
                    for (String subPath : subPaths) {
                        rotas.add(httpMethod.name() + " " + prefix + subPath);
                    }
                }
            }
        }
        return rotas;
    }
}
