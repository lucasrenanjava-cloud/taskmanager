package com.lucasrenan.taskmanager.agent.dto.response;
import com.lucasrenan.taskmanager.model.entity.MensagemChat;
import com.lucasrenan.taskmanager.model.enums.RoleChat;

import java.time.LocalDateTime;

public record MensagemChatResponse(
        Long id,
        RoleChat role,
        String conteudo,
        LocalDateTime criadoEm
) {
    public static MensagemChatResponse fromEntity(MensagemChat mensagem) {
        return new MensagemChatResponse(
                mensagem.getId(),
                mensagem.getRole(),
                mensagem.getConteudo(),
                mensagem.getCriadoEm()
        );
    }
}
