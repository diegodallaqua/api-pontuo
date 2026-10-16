-- Índices redundantes: a coluna mais à esquerda de um índice composto já atende
-- buscas e chaves estrangeiras por ela.
--   idx_answer_option_question (question_id)  -> uq_answer_option (question_id, letter)
--   idx_mock_exam_user (user_id)               -> idx_mock_exam_user_finished (user_id, finished_at)
ALTER TABLE answer_option DROP INDEX IF EXISTS idx_answer_option_question;
ALTER TABLE mock_exam DROP INDEX IF EXISTS idx_mock_exam_user;

-- A questão é classificada por tópico OU por matéria, nunca pelos dois (o
-- tópico já define a matéria) nem por nenhum.
--
-- O MariaDB não aceita CHECK sobre coluna de chave estrangeira com ação em
-- cascata, então as FKs de topic_id e subject_id perdem o ON UPDATE CASCADE.
-- Não faz falta: os ids são auto-incremento e nunca são alterados.
ALTER TABLE question
    DROP FOREIGN KEY fk_question_topic,
    DROP FOREIGN KEY fk_question_subject;

ALTER TABLE question
    ADD CONSTRAINT fk_question_topic FOREIGN KEY (topic_id) REFERENCES topic (id),
    ADD CONSTRAINT fk_question_subject FOREIGN KEY (subject_id) REFERENCES subject (id);

ALTER TABLE question
    ADD CONSTRAINT chk_question_topic_or_subject
        CHECK ((topic_id IS NULL) <> (subject_id IS NULL));

-- A alternativa marcada precisa ser da questão do vínculo. A chave composta
-- garante isso no banco; com answer_option_id nulo (questão sem resposta) ela
-- não é verificada. Sem ON UPDATE CASCADE de propósito: mover para outra
-- questão uma alternativa já respondida mudaria a questão do simulado.
ALTER TABLE answer_option
    ADD UNIQUE KEY uq_answer_option_id_question (id, question_id);

ALTER TABLE mock_exam_has_question
    DROP FOREIGN KEY fk_mehq_answer_option;

ALTER TABLE mock_exam_has_question
    DROP INDEX idx_mehq_answer_option,
    ADD KEY idx_mehq_answer_option (answer_option_id, question_id),
    ADD CONSTRAINT fk_mehq_answer_option
        FOREIGN KEY (answer_option_id, question_id) REFERENCES answer_option (id, question_id);

-- O boletim por matéria passa a ser calculado de mock_exam_has_question, com a
-- mesma regra de matéria do restante da análise de desempenho.
DROP TABLE mock_exam_subject_result;
