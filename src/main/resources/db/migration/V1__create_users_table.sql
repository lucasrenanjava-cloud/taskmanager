CREATE TABLE tb_users (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    nome        VARCHAR(100)  NOT NULL,
    email       VARCHAR(150)  NOT NULL UNIQUE,
    senha       VARCHAR(255)  NOT NULL,
    criado_em   TIMESTAMP     NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP
);