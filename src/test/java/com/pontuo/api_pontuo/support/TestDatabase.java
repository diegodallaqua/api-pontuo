package com.pontuo.api_pontuo.support;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Detecta se há um MariaDB escutando no mesmo host e porta de
 * spring.datasource.url. Usuário, senha e schema continuam vindo do .env e do
 * banco local: se o banco estiver no ar mas mal configurado, os testes de
 * integração falham em vez de serem pulados.
 */
public final class TestDatabase {

    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final int TIMEOUT_MILLIS = 500;

    private static final boolean AVAILABLE = probe();

    private TestDatabase() {
    }

    public static boolean isAvailable() {
        return AVAILABLE;
    }

    private static boolean probe() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(HOST, PORT), TIMEOUT_MILLIS);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
