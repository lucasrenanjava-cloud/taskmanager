package com.lucasrenan.taskmanager.dto.request;

import com.lucasrenan.taskmanager.model.enums.Prioridade;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TarefaRequest(
        @NotBlank
        String titulo,
        String descricao,
        Prioridade prioridade,
        LocalDate prazo
) {}