package com.lucasrenan.taskmanager.agent.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(@NotBlank String mensagem) {}