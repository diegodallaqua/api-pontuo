-- O novo índice é criado antes de remover o antigo: a chave estrangeira de
-- institution_id precisa de um índice que a cubra o tempo todo (o TiDB não
-- aceita trocar os dois no mesmo ALTER). Statements separados também são
-- aceitos pelo MariaDB.
ALTER TABLE entrance_exam
    ADD UNIQUE KEY uq_entrance_exam_stage (institution_id, year, name, stage);

ALTER TABLE entrance_exam
    DROP INDEX uq_entrance_exam;

ALTER TABLE entrance_exam
    RENAME INDEX uq_entrance_exam_stage TO uq_entrance_exam;
