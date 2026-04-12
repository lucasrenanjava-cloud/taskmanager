ALTER TABLE tb_tarefas DROP CONSTRAINT fk_tarefas_usuario;

ALTER TABLE tb_tarefas DROP COLUMN id;

ALTER TABLE tb_tarefas ADD COLUMN id BIGSERIAL PRIMARY KEY;

ALTER TABLE tb_tarefas ADD CONSTRAINT fk_tarefas_usuario
    FOREIGN KEY (usuario_id) REFERENCES tb_users(id);