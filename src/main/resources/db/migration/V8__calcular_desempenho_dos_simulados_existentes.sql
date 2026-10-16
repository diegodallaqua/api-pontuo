-- Congela a correção das respostas já dadas com o gabarito atual.
UPDATE mock_exam_has_question l
    JOIN answer_option a ON a.id = l.answer_option_id
SET l.is_correct = a.right_answer;

-- Até aqui acertos e aproveitamento vinham do cliente. Em simulado não
-- finalizado eles ainda não existem.
UPDATE mock_exam
SET answered_count = NULL,
    correct_count = NULL,
    accuracy = NULL
WHERE finished_at IS NULL;

-- Recalcula os simulados finalizados no servidor. Aproveitamento é
-- acertos / num_questions: questão em branco ou não vinculada conta como erro.
UPDATE mock_exam m
    LEFT JOIN (SELECT mock_exam_id,
                      COUNT(answer_option_id) AS answered,
                      SUM(COALESCE(is_correct, 0)) AS correct
               FROM mock_exam_has_question
               GROUP BY mock_exam_id) r ON r.mock_exam_id = m.id
SET m.answered_count = COALESCE(r.answered, 0),
    m.correct_count = COALESCE(r.correct, 0),
    m.accuracy = LEAST(ROUND(COALESCE(r.correct, 0) * 100 / m.num_questions, 2), 100)
WHERE m.finished_at IS NOT NULL;

INSERT INTO mock_exam_subject_result
    (mock_exam_id, subject_id, question_count, answered_count, correct_count, accuracy)
SELECT l.mock_exam_id,
       COALESCE(t.subject_id, q.subject_id),
       COUNT(*),
       COUNT(l.answer_option_id),
       SUM(COALESCE(l.is_correct, 0)),
       ROUND(SUM(COALESCE(l.is_correct, 0)) * 100 / COUNT(*), 2)
FROM mock_exam_has_question l
    JOIN mock_exam m ON m.id = l.mock_exam_id
    JOIN question q ON q.id = l.question_id
    LEFT JOIN topic t ON t.id = q.topic_id
WHERE m.finished_at IS NOT NULL
  AND COALESCE(t.subject_id, q.subject_id) IS NOT NULL
GROUP BY l.mock_exam_id, COALESCE(t.subject_id, q.subject_id);
