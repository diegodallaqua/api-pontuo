package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.config.R2Properties;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/**
 * Arquivos das imagens das questões no Cloudflare R2.
 *
 * <p>O arquivo não passa pela API: ela só assina uma URL de {@code PUT} e o
 * frontend envia direto ao bucket. A assinatura fixa o tipo e o tamanho
 * declarados, então o R2 recusa um arquivo diferente do que foi autorizado.
 */
@Service
public class ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(ImageStorageService.class);

    /** Tempo que o frontend tem para enviar o arquivo depois de pedir a URL. */
    static final Duration UPLOAD_EXPIRATION = Duration.ofMinutes(10);

    /** Só o que está sob este prefixo foi enviado pela API e pode ser apagado por ela. */
    static final String KEY_PREFIX = "question-images/";

    /** O R2 aceita qualquer região; "auto" é a recomendada pela Cloudflare. */
    private static final Region R2_REGION = Region.of("auto");

    /**
     * Endereçamento por caminho (endpoint/bucket/chave), como na documentação
     * do R2: dispensa DNS por bucket e aceita nomes de bucket com ponto.
     */
    private static final S3Configuration PATH_STYLE = S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build();

    private final R2Properties properties;
    private final S3Client client;
    private final S3Presigner presigner;

    @Autowired
    public ImageStorageService(R2Properties properties) {
        this(properties,
                properties.configured() ? buildClient(properties) : null,
                properties.configured() ? buildPresigner(properties) : null);
        if (!properties.configured()) {
            log.warn("Upload de imagens desligado: defina R2_ACCOUNT_ID, R2_ACCESS_KEY_ID, "
                    + "R2_SECRET_ACCESS_KEY, R2_BUCKET e R2_PUBLIC_URL.");
        }
    }

    ImageStorageService(R2Properties properties, S3Client client, S3Presigner presigner) {
        this.properties = properties;
        this.client = client;
        this.presigner = presigner;
    }

    /**
     * Assina o envio de um arquivo novo. A chave é gerada aqui, e não pelo
     * cliente, para um upload nunca sobrescrever a imagem de outra questão.
     *
     * @param contentType tipo {@code image/*} já validado no DTO
     */
    public PresignedUpload presignUpload(String contentType, long size) {
        if (presigner == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Upload de imagens indisponível: armazenamento não configurado no servidor.");
        }
        String extension = contentType.substring(contentType.indexOf('/') + 1);
        String key = KEY_PREFIX + UUID.randomUUID() + "." + extension;

        PutObjectRequest object = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(key)
                .contentType(contentType)
                .contentLength(size)
                .build();
        PresignedPutObjectRequest presigned = presigner.presignPutObject(request -> request
                .signatureDuration(UPLOAD_EXPIRATION)
                .putObjectRequest(object));

        return new PresignedUpload(
                presigned.url().toString(),
                Map.of(HttpHeaders.CONTENT_TYPE, contentType),
                properties.publicUrlOf(key),
                presigned.expiration());
    }

    /**
     * Apaga o arquivo de uma imagem. URLs de fora do bucket, como as cadastradas
     * à mão antes do upload direto, são ignoradas.
     *
     * <p>Falha do R2 só vai para o log: quando isto roda o registro já saiu do
     * banco, e um arquivo órfão custa menos do que devolver erro de uma exclusão
     * que já aconteceu.
     */
    public void delete(String url) {
        if (client == null || url == null) {
            return;
        }
        String base = properties.publicUrlOf("");
        if (!url.startsWith(base + KEY_PREFIX)) {
            return;
        }
        String key = url.substring(base.length());
        try {
            client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(key)
                    .build());
        } catch (SdkException ex) {
            log.warn("Não foi possível apagar {} do bucket; o arquivo ficou órfão.", key, ex);
        }
    }

    @PreDestroy
    void close() {
        if (client != null) {
            client.close();
        }
        if (presigner != null) {
            presigner.close();
        }
    }

    static S3Client buildClient(R2Properties properties) {
        return S3Client.builder()
                .endpointOverride(properties.endpoint())
                .region(R2_REGION)
                .credentialsProvider(credentials(properties))
                .serviceConfiguration(PATH_STYLE)
                // Recomendação da Cloudflare: o R2 não aceita todos os checksums
                // que o SDK passou a enviar por padrão.
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                .build();
    }

    static S3Presigner buildPresigner(R2Properties properties) {
        return S3Presigner.builder()
                .endpointOverride(properties.endpoint())
                .region(R2_REGION)
                .credentialsProvider(credentials(properties))
                .serviceConfiguration(PATH_STYLE)
                .build();
    }

    private static StaticCredentialsProvider credentials(R2Properties properties) {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(properties.accessKeyId(), properties.secretAccessKey()));
    }
}
