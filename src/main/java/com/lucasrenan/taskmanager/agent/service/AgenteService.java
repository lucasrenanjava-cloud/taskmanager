package com.lucasrenan.taskmanager.agent.service;


import com.lucasrenan.taskmanager.agent.dto.response.MensagemChatResponse;
import com.lucasrenan.taskmanager.agent.config.TarefaAgent;
import com.lucasrenan.taskmanager.model.entity.User;
import com.lucasrenan.taskmanager.repository.MensagemChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class AgenteService {

    private final TarefaAgent tarefaAgent;
    private final MensagemChatRepository mensagemChatRepository;

    public AgenteService(TarefaAgent tarefaAgent,
                         MensagemChatRepository mensagemChatRepository) {
        this.tarefaAgent = tarefaAgent;
        this.mensagemChatRepository = mensagemChatRepository;
    }

    public String chat(String mensagem, User usuario) {
        return tarefaAgent.chat(mensagem, usuario);
    }

    public List<MensagemChatResponse> listarHistorico(User usuario) {
        return mensagemChatRepository
                .findByUsuarioIdOrderByCriadoEmAsc(usuario.getId())
                .stream()
                .map(MensagemChatResponse::fromEntity)
                .toList();
    }

    public void limparHistorico(User usuario) {
        mensagemChatRepository.deleteByUsuarioId(usuario.getId());
    }
}
