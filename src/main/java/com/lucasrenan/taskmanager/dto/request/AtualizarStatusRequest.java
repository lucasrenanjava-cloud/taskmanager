package com.lucasrenan.taskmanager.dto.request;

import com.lucasrenan.taskmanager.model.enums.StatusTarefa;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(
        @NotNull
        StatusTarefa status
) {}
