package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.Topic;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.entity.UserRole;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDate;


@TestComponent
public class DatabaseFixtures {

    public static final long ENEM_PRIMEIRO_DIA_ID = 1L;
    public static final long BIOLOGIA_ID = 5L;
    public static final long QUIMICA_ID = 7L;
    public static final long GENETICA_ID = 34L;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final QuestionRepository questionRepository;
    private final EntranceExamRepository entranceExamRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final MockExamRepository mockExamRepository;

    public DatabaseFixtures(UserRepository userRepository,
                            UserRoleRepository userRoleRepository,
                            QuestionRepository questionRepository,
                            EntranceExamRepository entranceExamRepository,
                            SubjectRepository subjectRepository,
                            TopicRepository topicRepository,
                            AnswerOptionRepository answerOptionRepository,
                            MockExamRepository mockExamRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.questionRepository = questionRepository;
        this.entranceExamRepository = entranceExamRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.mockExamRepository = mockExamRepository;
    }

    public User estudante(String username) {
        UserRole estudante = userRoleRepository.findByDescription("Estudante")
                .orElseThrow(() -> new IllegalStateException("migration V2 não carregou o perfil Estudante"));
        return userRepository.save(new User(username, username + "@pontuo.com",
                LocalDate.of(2005, 1, 1), "$2a$10$hash-de-teste", estudante));
    }

    /** Questão de Biologia classificada pela matéria, sem tópico. */
    public Question questao(String enunciado) {
        return questaoDaMateria(enunciado, BIOLOGIA_ID, (short) 2);
    }

    public Question questaoDaMateria(String enunciado, long subjectId, short dificuldade) {
        Subject materia = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalStateException("migration V2 não carregou a matéria id=" + subjectId));
        return questionRepository.save(new Question(enunciado, null, dificuldade, enem(), null, materia));
    }

    public Question questaoDoTopico(String enunciado, long topicId, short dificuldade) {
        Topic topico = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalStateException("migration V2 não carregou o tópico id=" + topicId));
        return questionRepository.save(new Question(enunciado, null, dificuldade, enem(), topico, null));
    }

    /** Cria as alternativas A (correta) e B (errada) e devolve as duas, nessa ordem. */
    public AnswerOption[] alternativas(Question questao) {
        return new AnswerOption[] {
                answerOptionRepository.save(new AnswerOption("A", "Alternativa correta", true, questao)),
                answerOptionRepository.save(new AnswerOption("B", "Alternativa errada", false, questao))
        };
    }

    public MockExam simulado(String nome, int numQuestions, User dono) {
        return mockExamRepository.save(new MockExam(nome, (short) numQuestions, (short) 90, dono));
    }

    private EntranceExam enem() {
        return entranceExamRepository.findById(ENEM_PRIMEIRO_DIA_ID)
                .orElseThrow(() -> new IllegalStateException("migration V2 não carregou o ENEM id=1"));
    }
}
