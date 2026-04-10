package com.lucasrenan.taskmanager.exception;

public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException() {
        super("Você não tem permissão para acessar esta tarefa");
    }
}
