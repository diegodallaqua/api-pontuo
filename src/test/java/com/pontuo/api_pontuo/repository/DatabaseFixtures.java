package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.entity.UserRole;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDate;


@TestComponent
public class DatabaseFixtures {

    private static final long ENEM_PRIMEIRO_DIA_ID = 1L;
    private static final long BIOLOGIA_ID = 5L;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final QuestionRepository questionRepository;
    private final EntranceExamRepository entranceExamRepository;
    private final SubjectRepository subjectRepository;
    private final MockExamRepository mockExamRepository;

    public DatabaseFixtures(UserRepository userRepository,
                            UserRoleRepository userRoleRepository,
                            QuestionRepository questionRepository,
                            EntranceExamRepository entranceExamRepository,
                            SubjectRepository subjectRepository,
                            MockExamRepository mockExamRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.questionRepository = questionRepository;
        this.entranceExamRepository = entranceExamRepository;
        this.subjectRepository = subjectRepository;
        this.mockExamRepository = mockExamRepository;
    }

    public User estudante(String username) {
        UserRole estudante = userRoleRepository.findByDescription("Estudante")
                .orElseThrow(() -> new IllegalStateException("data.sql não carregou o perfil Estudante"));
        return userRepository.save(new User(username, username + "@pontuo.com",
                LocalDate.of(2005, 1, 1), "$2a$10$hash-de-teste", estudante));
    }

    public Question questao(String enunciado) {
        EntranceExam enem = entranceExamRepository.findById(ENEM_PRIMEIRO_DIA_ID)
                .orElseThrow(() -> new IllegalStateException("data.sql não carregou o ENEM id=1"));
        Subject biologia = subjectRepository.findById(BIOLOGIA_ID)
                .orElseThrow(() -> new IllegalStateException("data.sql não carregou Biologia id=5"));
        return questionRepository.save(new Question(enunciado, null, (short) 2, enem, null, biologia));
    }

    public MockExam simulado(String nome, int numQuestions, User dono) {
        return mockExamRepository.save(new MockExam(nome, (short) numQuestions, (short) 90, dono));
    }
}
