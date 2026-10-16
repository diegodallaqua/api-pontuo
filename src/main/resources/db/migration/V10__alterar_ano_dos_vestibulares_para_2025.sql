-- Os vestibulares de referência (V2 e V5) passam a ser os de 2025, edição cujas
-- provas e gabaritos já foram publicados. As migrations originais não são
-- editadas porque já foram aplicadas: o Flyway recusaria o checksum alterado.
UPDATE entrance_exam
SET year = 2025
WHERE id BETWEEN 1 AND 16
  AND year = 2026;
