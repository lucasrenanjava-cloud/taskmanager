package com.lucasrenan.taskmanager.agent.config;

import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface TarefaAssistant {

    @SystemMessage("""
            Você é um assistente de gerenciamento de tarefas.
            Ajude o usuário a criar, listar, atualizar e organizar suas tarefas.
            Sempre confirme as ações realizadas de forma clara e objetiva.
            Quando listar tarefas, sempre mostre o ID numérico de cada uma pois o usuário pode precisar dele.
            Se faltar algum dado para executar uma ação, solicite apenas o que falta.
            """)
    Result<String> chat(@UserMessage String mensagem);
}
