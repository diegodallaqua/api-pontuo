package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.config.R2Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * A assinatura é calculada localmente pelo SDK, sem rede, então dá para
 * conferir a URL gerada com credenciais falsas.
 */
@DisplayName("ImageStorageService")
class ImageStorageServiceTest {

    private static final R2Properties CONFIGURADO = new R2Properties(
            "conta123", "chave-de-acesso", "segredo-de-acesso", "pontuo-imagens", "https://img.pontuo.app/");

    private static final R2Properties SEM_CONFIGURACAO = new R2Properties("", "", "", "", "");

    private S3Client client;
    private ImageStorageService service;

    @BeforeEach
    void setUp() {
        client = mock(S3Client.class);
        service = new ImageStorageService(CONFIGURADO, client, ImageStorageService.buildPresigner(CONFIGURADO));
    }

    @AfterEach
    void tearDown() {
        service.close();
    }

    @Nested
    @DisplayName("presignUpload")
    class PresignUpload {

        @Test
        @DisplayName("assina um PUT no bucket com tipo e tamanho do arquivo")
        void deveAssinarPut() {
            Instant antes = Instant.now();

            PresignedUpload upload = service.presignUpload("image/png", 2048);

            URI uploadUrl = URI.create(upload.uploadUrl());
            String chave = upload.url().substring("https://img.pontuo.app/".length());
            assertAll(
                    () -> assertEquals("conta123.r2.cloudflarestorage.com", uploadUrl.getHost()),
                    () -> assertEquals("/pontuo-imagens/" + chave, uploadUrl.getPath()),
                    () -> assertTrue(chave.matches("question-images/[0-9a-f-]{36}\\.png"), chave),
                    () -> assertTrue(uploadUrl.getQuery().contains("X-Amz-SignedHeaders=content-length;content-type;host"),
                            uploadUrl.getQuery()),
                    () -> assertFalse(uploadUrl.getQuery().contains("checksum"), uploadUrl.getQuery()),
                    () -> assertEquals(Map.of("Content-Type", "image/png"), upload.headers()),
                    () -> assertFalse(upload.expiresAt().isBefore(antes.plus(Duration.ofMinutes(10)))));
        }

        @Test
        @DisplayName("cada pedido gera uma chave nova")
        void deveGerarChavesDiferentes() {
            assertFalse(service.presignUpload("image/jpeg", 10).url()
                    .equals(service.presignUpload("image/jpeg", 10).url()));
        }

        @Test
        @DisplayName("responde 503 quando o R2 não está configurado")
        void deveRecusarSemConfiguracao() {
            ImageStorageService semR2 = new ImageStorageService(SEM_CONFIGURACAO);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> semR2.presignUpload("image/png", 2048));

            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("apaga o arquivo enviado pela API")
        void deveApagarArquivoDoBucket() {
            service.delete("https://img.pontuo.app/question-images/abc.png");

            ArgumentCaptor<DeleteObjectRequest> request = ArgumentCaptor.forClass(DeleteObjectRequest.class);
            verify(client).deleteObject(request.capture());
            assertAll(
                    () -> assertEquals("pontuo-imagens", request.getValue().bucket()),
                    () -> assertEquals("question-images/abc.png", request.getValue().key()));
        }

        @Test
        @DisplayName("ignora url de fora do bucket ou fora do prefixo das imagens")
        void deveIgnorarUrlExterna() {
            service.delete("https://cdn.pontuo.com/grafico.png");
            service.delete("https://img.pontuo.app/outra-pasta/abc.png");

            verifyNoInteractions(client);
        }

        @Test
        @DisplayName("falha do R2 não interrompe a exclusão")
        void deveEngolirFalhaDoR2() {
            when(client.deleteObject(any(DeleteObjectRequest.class)))
                    .thenThrow(SdkClientException.create("sem rede"));

            assertDoesNotThrow(() -> service.delete("https://img.pontuo.app/question-images/abc.png"));
        }

        @Test
        @DisplayName("não faz nada quando o R2 não está configurado")
        void deveIgnorarSemConfiguracao() {
            ImageStorageService semR2 = new ImageStorageService(SEM_CONFIGURACAO);

            assertDoesNotThrow(() -> semR2.delete("https://img.pontuo.app/question-images/abc.png"));
        }
    }
}
