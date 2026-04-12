package com.lucasrenan.taskmanager.agent.tools;

import com.lucasrenan.taskmanager.agent.config.TarefaAssistant;
import com.lucasrenan.taskmanager.model.entity.MensagemChat;
import com.lucasrenan.taskmanager.model.entity.User;
import com.lucasrenan.taskmanager.model.enums.RoleChat;
import com.lucasrenan.taskmanager.repository.MensagemChatRepository;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TarefaAgent {

    private final GoogleAiGeminiChatModel chatModel;
    private final TarefaTools tarefaTools;
    private final MensagemChatRepository mensagemChatRepository;

    public TarefaAgent(GoogleAiGeminiChatModel chatModel,
                       TarefaTools tarefaTools,
                       MensagemChatRepository mensagemChatRepository) {
        this.chatModel = chatModel;
        this.tarefaTools = tarefaTools;
        this.mensagemChatRepository = mensagemChatRepository;
    }

    public String chat(String mensagemUsuario, User usuario) {
        try {
            tarefaTools.setUsuarioAtual(usuario);

            List<MensagemChat> historico = mensagemChatRepository
                    .findByUsuarioIdOrderByCriadoEmAsc(usuario.getId());

            MessageWindowChatMemory memory = MessageWindowChatMemory.withMaxMessages(50);
            for (MensagemChat msg : historico) {
                if (msg.getRole() == RoleChat.USER) {
                    memory.add(UserMessage.from(msg.getConteudo()));
                } else {
                    memory.add(AiMessage.from(msg.getConteudo()));
                }
            }

            TarefaAssistant assistant = AiServices.builder(TarefaAssistant.class)
                    .chatLanguageModel(chatModel)
                    .tools(tarefaTools)
                    .chatMemory(memory)
                    .build();

            String resposta = assistant.chat(mensagemUsuario).content();

            MensagemChat msgUsuario = new MensagemChat();
            msgUsuario.setRole(RoleChat.USER);
            msgUsuario.setConteudo(mensagemUsuario);
            msgUsuario.setUsuario(usuario);
            mensagemChatRepository.save(msgUsuario);

            MensagemChat msgAgente = new MensagemChat();
            msgAgente.setRole(RoleChat.ASSISTANT);
            msgAgente.setConteudo(resposta);
            msgAgente.setUsuario(usuario);
            mensagemChatRepository.save(msgAgente);

            return resposta;

        } finally {
            tarefaTools.limparUsuarioAtual();
        }
    }
}