package com.lucasrenan.taskmanager.exception;

public class TarefaNaoEncontradaException extends RuntimeException {
    public TarefaNaoEncontradaException(Long id) {
        super("Tarefa não encontrada com id: " + id);
    }
}
