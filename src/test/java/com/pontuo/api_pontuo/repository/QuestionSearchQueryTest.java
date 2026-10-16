package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.Address;
import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.Institution;
import com.pontuo.api_pontuo.entity.KnowledgeArea;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.Topic;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * As consultas de busca de questões são JPQL escrito à mão, então um erro nelas
 * só apareceria quando a aplicação subisse. Este teste manda o Hibernate
 * compilar cada uma sem precisar de banco, para a CI pegar o erro.
 */
@DisplayName("Consultas de busca de questões")
class QuestionSearchQueryTest {

    private static SessionFactory sessionFactory;

    @BeforeAll
    static void bootstrapHibernate() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(Map.of(
                        "hibernate.dialect", "org.hibernate.dialect.MariaDBDialect",
                        // Sem banco por trás: o teste só compila HQL.
                        "hibernate.boot.allow_jdbc_metadata_access", "false"))
                .build();
        sessionFactory = new MetadataSources(registry)
                .addAnnotatedClass(Question.class)
                .addAnnotatedClass(AnswerOption.class)
                .addAnnotatedClass(EntranceExam.class)
                .addAnnotatedClass(Institution.class)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(State.class)
                .addAnnotatedClass(Topic.class)
                .addAnnotatedClass(Subject.class)
                .addAnnotatedClass(KnowledgeArea.class)
                .buildMetadata()
                .buildSessionFactory();
    }

    @AfterAll
    static void closeHibernate() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    private void compila(String jpql) {
        try (Session session = sessionFactory.openSession()) {
            assertDoesNotThrow(() -> session.createQuery(jpql, Object.class));
        }
    }

    @Test
    @DisplayName("a consulta da página compila com os joins de fetch")
    void deveCompilarAConsultaDaPagina() {
        compila("SELECT q FROM Question q "
                + QuestionRepository.SEARCH_FETCH_JOINS
                + QuestionRepository.SEARCH_WHERE);
    }

    @Test
    @DisplayName("a contagem compila com os mesmos filtros")
    void deveCompilarAContagem() {
        compila("SELECT count(q) FROM Question q "
                + QuestionRepository.SEARCH_JOINS
                + QuestionRepository.SEARCH_WHERE);
    }

    @Test
    @DisplayName("a consulta de ids do sorteio compila")
    void deveCompilarAConsultaDeIds() {
        compila("SELECT q.id FROM Question q "
                + QuestionRepository.SEARCH_JOINS
                + QuestionRepository.SEARCH_WHERE);
    }
}
