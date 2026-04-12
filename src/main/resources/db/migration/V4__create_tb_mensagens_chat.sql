CREATE TABLE tb_mensagens_chat (
    id          BIGSERIAL     PRIMARY KEY,
    role        VARCHAR(20)   NOT NULL,
    conteudo    TEXT          NOT NULL,
    usuario_id  UUID          NOT NULL,
    criado_em   TIMESTAMP     NOT NULL DEFAULT now(),

    CONSTRAINT fk_mensagens_usuario FOREIGN KEY (usuario_id) REFERENCES tb_users(id)
);