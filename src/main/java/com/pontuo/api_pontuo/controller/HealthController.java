package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.HealthResponseDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verificação de vida para o balanceador e o monitoramento. Responder já
 * prova que o servidor está de pé; de propósito não consulta o banco nem
 * nenhuma outra dependência, para ser barato e não derrubar a instância
 * quando só o banco estiver fora.
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public HealthResponseDTO health() {
        return HealthResponseDTO.UP;
    }
}
