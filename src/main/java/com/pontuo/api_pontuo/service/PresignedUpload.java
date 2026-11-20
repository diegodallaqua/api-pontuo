package com.pontuo.api_pontuo.service;

import java.time.Instant;
import java.util.Map;

/**
 * Autorização para o frontend enviar um arquivo direto ao bucket.
 *
 * @param uploadUrl URL assinada que recebe o arquivo por {@code PUT}
 * @param headers   headers que o {@code PUT} precisa repetir, porque fazem parte da assinatura
 * @param url       endereço público do arquivo, a ser cadastrado na imagem da questão
 * @param expiresAt instante a partir do qual a {@code uploadUrl} deixa de valer
 */
public record PresignedUpload(
        String uploadUrl,
        Map<String, String> headers,
        String url,
        Instant expiresAt
) {
}
