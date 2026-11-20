-- A legenda da imagem é opcional na API (DTO, entidade e openapi.yaml), mas a
-- V1 criou a coluna como NOT NULL. Cadastrar imagem sem legenda falhava no
-- INSERT e chegava ao cliente como o 409 genérico de integridade.
ALTER TABLE question_image MODIFY `subtitle` varchar(255) NULL;
