CREATE TABLE tb_tarefas (
    id            UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo        VARCHAR(255)  NOT NULL,
    descricao     TEXT,
    prioridade    VARCHAR(20)   NOT NULL DEFAULT 'MEDIA',
    status        VARCHAR(20)   NOT NULL DEFAULT 'PENDENTE',
    prazo         DATE,
    usuario_id    UUID          NOT NULL,
    criado_em     TIMESTAMP     NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP,

    CONSTRAINT fk_tarefas_usuario FOREIGN KEY (usuario_id) REFERENCES tb_users(id)
);