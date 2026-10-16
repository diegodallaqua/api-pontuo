-- Correção congelada no momento da resposta. Se o gabarito da alternativa
-- mudar depois, o histórico do estudante não muda junto. Fica NULL enquanto a
-- questão não foi respondida.
ALTER TABLE mock_exam_has_question
    ADD COLUMN is_correct tinyint(1) DEFAULT NULL AFTER answered_at;

-- Questões respondidas no simulado, calculado na finalização. Separa a questão
-- em branco da questão errada.
ALTER TABLE mock_exam
    ADD COLUMN answered_count smallint(6) DEFAULT NULL AFTER finished_at;

-- Resultado por matéria de cada simulado finalizado, gravado uma única vez na
-- finalização. A matéria é a do tópico da questão ou, sem tópico, a da própria
-- questão.
CREATE TABLE `mock_exam_subject_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mock_exam_id` bigint(20) NOT NULL,
  `subject_id` bigint(20) NOT NULL,
  `question_count` smallint(6) NOT NULL,
  `answered_count` smallint(6) NOT NULL,
  `correct_count` smallint(6) NOT NULL,
  `accuracy` decimal(5,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_mesr` (`mock_exam_id`,`subject_id`),
  KEY `idx_mesr_subject` (`subject_id`),
  CONSTRAINT `fk_mesr_mock_exam` FOREIGN KEY (`mock_exam_id`) REFERENCES `mock_exam` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_mesr_subject` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
