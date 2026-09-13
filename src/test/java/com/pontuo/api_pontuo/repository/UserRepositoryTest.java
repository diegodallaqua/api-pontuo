package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.support.RequiresDatabase;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(DatabaseFixtures.class)
@RequiresDatabase
@DisplayName("UserRepository (MariaDB)")
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private DatabaseFixtures fixtures;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void cadastrarMaria() {
        fixtures.estudante("junit_maria");
    }

    @Test
    @DisplayName("collation _ci: busca ignora maiúsculas, então UserService.update recusa trocar só a caixa do próprio username")
    void deveIgnorarMaiusculasComCollationCi() {
        assumeTrue(collationDoUsername().endsWith("_ci"), "coluna users.username não usa collation _ci");

        assertAll(
                () -> assertTrue(repository.findByUsername("JUNIT_MARIA").isPresent()),
                () -> assertTrue(repository.existsByUsername("Junit_Maria")));
    }

    @Test
    @DisplayName("collation sensível a maiúsculas: JUNIT_MARIA é outro usuário")
    void deveDiferenciarMaiusculasComCollationSensivel() {
        assumeFalse(collationDoUsername().endsWith("_ci"), "coluna users.username usa collation _ci");

        assertTrue(repository.findByUsername("JUNIT_MARIA").isEmpty());
    }

    @Test
    @DisplayName("login por email encontra o usuário cadastrado")
    void deveEncontrarPorEmail() {
        assertTrue(repository.findByEmail("junit_maria@pontuo.com").isPresent());
    }

    private String collationDoUsername() {
        return (String) entityManager.createNativeQuery("""
                        SELECT COLLATION_NAME FROM information_schema.COLUMNS
                        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'username'
                        """)
                .getSingleResult();
    }
}
