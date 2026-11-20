package com.pontuo.api_pontuo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.stream.Stream;

/**
 * Bucket do Cloudflare R2 onde ficam as imagens das questões. As credenciais
 * só assinam as URLs e apagam arquivos; o frontend nunca as recebe.
 *
 * @param publicUrl endereço público do bucket (domínio r2.dev ou próprio), base
 *                  das URLs gravadas em {@code question_image.url}
 */
@ConfigurationProperties(prefix = "storage.r2")
public record R2Properties(
        String accountId,
        String accessKeyId,
        String secretAccessKey,
        String bucket,
        String publicUrl
) {

    /** O upload só funciona com todas as variáveis preenchidas. */
    public boolean configured() {
        return Stream.of(accountId, accessKeyId, secretAccessKey, bucket, publicUrl)
                .allMatch(StringUtils::hasText);
    }

    /** Endpoint da API S3 do R2, para onde vão o upload e a exclusão. */
    public URI endpoint() {
        return URI.create("https://" + accountId + ".r2.cloudflarestorage.com");
    }

    /** Endereço pelo qual o arquivo é lido depois de enviado. */
    public String publicUrlOf(String key) {
        return StringUtils.trimTrailingCharacter(publicUrl, '/') + "/" + key;
    }
}
