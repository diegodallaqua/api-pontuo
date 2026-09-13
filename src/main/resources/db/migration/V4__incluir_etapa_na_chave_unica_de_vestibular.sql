ALTER TABLE entrance_exam
    DROP INDEX uq_entrance_exam,
    ADD UNIQUE KEY uq_entrance_exam (institution_id, year, name, stage);
