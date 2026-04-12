package com.lucasrenan.taskmanager.dto.response;

import com.lucasrenan.taskmanager.model.entity.Tarefa;
import com.lucasrenan.taskmanager.model.enums.Prioridade;
import com.lucasrenan.taskmanager.model.enums.StatusTarefa;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TarefaResponse(
        Long id,
        String titulo,
        String descricao,
        Prioridade prioridade,
        StatusTarefa status,
        LocalDate prazo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public static TarefaResponse fromEntity(Tarefa tarefa) {
        return new TarefaResponse(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getPrioridade(),
                tarefa.getStatus(),
                tarefa.getPrazo(),
                tarefa.getCriadoEm(),
                tarefa.getAtualizadoEm()
        );
    }
}
